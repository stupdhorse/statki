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

    void boardAction(Boat boat) {

        try {
            boat.semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        boat.boardingLock.lock();
        // tutaj czas wjazdu samochodu
        try {
            sleep(100);
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        boat.capacityLock.lock();
        boat.currentCapacity++;
        if(boat.currentCapacity == boat.capacity_) boat.capacityCondition.signal();
        boat.capacityLock.unlock();

        boat.boardingLock.unlock();

    }


    public void getOnBoard(Boat boat) {
        this.boat = boat;

        checkForBoatStatus(boat, BoatStatus.Boarding);
        boardAction(boat);
        System.out.println("Car [" + getName()  + "] got on board");
    }

    public void getFromBoard(Boat boat) {
        checkForBoatStatus(boat, BoatStatus.Releasing);
        boat.semaphore.release();
        boat.boardingLock.lock();
        // tutaj czas wjazdu samochodu
        try {
            sleep(100);
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        boat.capacityLock.lock();
        boat.currentCapacity--;
        if(boat.currentCapacity == 0) boat.capacityCondition.signal();
        boat.capacityLock.unlock();
        boat.boardingLock.unlock();

        System.out.println("Car [" + getName()  + "] got from board");


        this.boat = null;
    }

    private void checkForBoatStatus(Boat boat, BoatStatus status) {
        boat.lock.lock();
        try {
            while (boat.status != status) {
                boat.statusCondition.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            boat.lock.unlock();
        }
    }

    public void run() {

        try {
            harbor.semaphore.acquire();
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }


        getOnBoard(harbor.currentBoat);
        getFromBoard(boat);
    }
}
