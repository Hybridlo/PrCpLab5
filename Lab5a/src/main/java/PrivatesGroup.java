import java.util.ArrayList;
import java.util.List;

public class PrivatesGroup implements Runnable {
    List<Boolean> privates;
    Barrier barrier;
    int begin;
    int end;

    PrivatesGroup(Barrier barrier, int begin, int end, List<Boolean> privates) {
        this.barrier = barrier;
        this.privates = privates;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public void run() {
         int count = 0;
         int turns = 0;
         while (!Thread.interrupted()) {
             count++;
             List<Boolean> newPrivates = new ArrayList<>();
             boolean skip = false;
             for (int i = begin; i < end + 1; i++) {
                 if (skip) {
                     skip = false;
                 }

                 else if ((i == 0 && privates.get(i)) || (i == privates.size() - 1 && !privates.get(i))) {
                     newPrivates.add(privates.get(i));
                 }

                 else if (i == begin && privates.get(i) && !privates.get(i - 1)) {   //if start of this array part looks
                     newPrivates.add(false);
                     turns++;
                 }

                 else if (!privates.get(i) && privates.get(i + 1)) {
                     newPrivates.add(true);
                     turns++;

                     if (i + 1 != end + 1) {        //if i + 1 is not out of array limit
                         newPrivates.add(false);
                         turns++;
                     }

                     skip = true;
                 }
                 else {
                     newPrivates.add(privates.get(i));
                 }
             }

             try {
                 barrier.await();
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }

             for (int i = begin; i < end + 1; i++) {    //update main array
                 privates.set(i, newPrivates.get(0));
                 newPrivates.remove(0);
             }

             try {
                 barrier.await();
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }

             boolean checked = true;
             boolean way = privates.get(0);

             for (int i = 0; i < privates.size(); i++) {    //check main array
                 if (privates.get(i) != way) {
                     for (int j = i; j < privates.size(); j++) {
                         if (privates.get(j) == way) {
                             checked = false;
                             break;
                         }
                     }
                     break;
                 }
             }

             if (checked) {
                 break;
             }

             try {
                 barrier.await();
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }

         System.out.println("Thread: " + Thread.currentThread().getId() + ", operations completed: " + count
                            + ", turns done: " + turns);
         barrier.decrement();
    }
}
