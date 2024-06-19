package PW;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.locks.Condition;

public class Car extends Thread {

    public Car(Harbor harbor) {
        this.harbor = harbor;
         velocity = 10;

    }

    int velocity;
    Harbor harbor;
    Boat boat;


    public void getOnBoard(Boat boat) {
        this.boat = boat;

        boat.lock.lock();
        try {
            while (boat.status != BoatStatus.Boarding) {
                boat.condition.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            boat.lock.unlock();
        }

        System.out.println("AUTO WZIELO SEMAFOR");


        try {
            boat.semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("AUTO WZIELO SEMAFOR");

        try {
            boat.releasingBarrier.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Car got on board");

    }

    public void getFromBoard(Boat boat) {

        boat.lock.lock();
        try {
            while (boat.status != BoatStatus.Releasing) {
                boat.condition.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            boat.lock.unlock();
        }

        boat.semaphore.release();

        this.boat = null;
    }

    public void run() {

        try {
            harbor.semaphore.acquire();
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }

        //przyjezdza statek

        getOnBoard(harbor.currentBoat);
        getFromBoard(boat);




    }
}
