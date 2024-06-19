package PW;

public class Notfier {

    private boolean signal = false;

    public synchronized void doWait() {
        while (!signal) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted");
            }
        }
        // Reset the signal for the next wait
        signal = false;
    }

    public synchronized void doNotify() {
        signal = true;
        notify();
    }
}
