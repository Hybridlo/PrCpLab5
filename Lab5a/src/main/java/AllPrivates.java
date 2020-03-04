import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AllPrivates {
    List<Boolean> privates;
    int threadAmount;

    AllPrivates(int threadAmount) {
        this.threadAmount = threadAmount;
    }

    void init() {
        privates = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        final int privateAmount = 50;

        for (int i = 0; i < privateAmount * threadAmount; i++) {
            privates.add(random.nextBoolean());
        }

        Barrier barrier = new Barrier(threadAmount);

        for (int i = 0; i < threadAmount; i++) {
            PrivatesGroup groupThread = new PrivatesGroup(barrier, i * 50, i * 50 + 49, privates);
            Thread childThread = new Thread(groupThread);
            childThread.start();
        }

        while (barrier.amount > 0) {
            Thread.yield();
        }
    }
}
