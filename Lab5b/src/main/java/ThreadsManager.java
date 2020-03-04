import java.util.concurrent.CyclicBarrier;

public class ThreadsManager {
    CyclicBarrier barrier;
    StringThread sThread1 = new StringThread(this);
    StringThread sThread2 = new StringThread(this);
    StringThread sThread3 = new StringThread(this);
    StringThread sThread4 = new StringThread(this);

    ThreadsManager() {
        this.barrier = new CyclicBarrier(4);
    }

    void init() throws InterruptedException {
        Thread thread1 = new Thread(sThread1);
        Thread thread2 = new Thread(sThread2);
        Thread thread3 = new Thread(sThread3);
        Thread thread4 = new Thread(sThread4);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
    }

    boolean checkSumEquality(int sum) {
        return (sum == sThread1.abSum && sum == sThread2.abSum && sum == sThread3.abSum && sum == sThread4.abSum);
    }
}
