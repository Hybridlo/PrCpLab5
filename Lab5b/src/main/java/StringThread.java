import java.util.Random;
import java.util.concurrent.BrokenBarrierException;

public class StringThread implements Runnable {
    String myString = "";
    ThreadsManager manager;
    int abSum = -1;

    StringThread(ThreadsManager manager) {
        this.manager = manager;

        final int strLength = 50;
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < strLength; i++) {
            int rInt = random.nextInt(4);

            switch (rInt) {
                case 0:
                    myString += "a";
                    break;
                case 1:
                    myString += "b";
                    break;
                case 2:
                    myString += "c";
                    break;
                case 3:
                    myString += "d";
                    break;
            }

        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            Random random = new Random(System.currentTimeMillis());
            for (int i = 0; i < myString.length(); i++) {
                boolean choice = random.nextBoolean();
                if (choice) {
                    changeCharAt(i);
                }
            }

            calcABSum();

            try {
                manager.barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }

            if (manager.checkSumEquality(abSum)) {
                break;
            }
            else {
                abSum = -1;
            }

            try {
                manager.barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Final string of thread " + Thread.currentThread().getId() + ": " + myString
                            + " sum = " + abSum);
    }

    private void changeCharAt(int i) {
        char c = myString.charAt(i);
        switch (c) {
            case 'a':
                myString = myString.substring(0, i) + "c" + myString.substring(i + 1);
                break;
            case 'b':
                myString = myString.substring(0, i) + "d" + myString.substring(i + 1);
                break;
            case 'c':
                myString = myString.substring(0, i) + "a" + myString.substring(i + 1);
                break;
            case 'd':
                myString = myString.substring(0, i) + "b" + myString.substring(i + 1);
                break;
        }
    }

    private void calcABSum() {
        abSum = 0;
        for (int i = 0; i < myString.length(); i++) {
            if (myString.charAt(i) == 'a' || myString.charAt(i) == 'b') {
                abSum++;
            }
        }
    }
}
