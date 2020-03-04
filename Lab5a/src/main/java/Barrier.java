public class Barrier {
    int amount;
    int currAmount = 0;

    Barrier(int amount) {
        this.amount = amount;
    }

    synchronized void await() throws InterruptedException {
        currAmount++;

        if (currAmount < amount) {
            wait();
        }
        else {
            checkAmount();
        }
    }

    synchronized void decrement() {
        amount--;
        checkAmount();
    }

    void checkAmount() {
        if (currAmount == amount) {
            currAmount = 0;
            notifyAll();
        }
    }
}
