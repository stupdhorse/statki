package PW;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

enum BoatStatus {
    Waiting,
    Boarding,
    Releasing,
    Moving,
}


public class Boat extends Thread {
    public Boat(int capacity,Harbor harborA, Harbor harborB) {
        capacity_ = capacity;
        distance_ = 100;
        velocity_ = 10;
        harbors[0] = harborA;
        harbors[1] = harborB;
        destination = 1;
        semaphore = new Semaphore(capacity,true);
        status =  BoatStatus.Moving;
        boardingBarrier = new CyclicBarrier(capacity);
        currentCapacity = 0;
    }

    private static final int WAIT_TIME_MS = 10_000;
    int capacity_;
    int distance_;
    int velocity_;
    int destination;
    Semaphore semaphore;
    BoatStatus status;

    final CyclicBarrier boardingBarrier;

    int currentCapacity;
    final Lock capacityLock = new ReentrantLock(true);
    final Condition capacityCondition = capacityLock.newCondition();

    final Lock boardingLock = new ReentrantLock(true);

    final Lock lock = new ReentrantLock(true);
    final Condition statusCondition = lock.newCondition();
    Harbor[] harbors = new Harbor[2];

    void resetDistance() {
        distance_ = 50;
    }
    void travel() {
        resetDistance();
        while(distance_ > 0) {
            distance_ -= velocity_;
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        destination = 1 - destination;
    }

    void update_status(BoatStatus newStatus) {
        lock.lock();
        try {
            status = newStatus;
            statusCondition.signalAll();
        } finally {
            lock.unlock();//interrupt or not, release lock
        }
    }

    void release_cars() {
        System.out.println("REALEASING");

        update_status(BoatStatus.Releasing);

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


//        if(releasingBarrier == null) {return;}
//        try {
//            releasingBarrier.await();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } catch (BrokenBarrierException ignored) {
//        }
    }

    void wait_for_boarding_cars() {
        capacityLock.lock();
        while (currentCapacity != capacity_) {
            Date deadline = new Date(System.currentTimeMillis() + WAIT_TIME_MS);
            try {
                capacityCondition.awaitUntil(deadline);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                capacityLock.unlock();
            }
        }
    }

    void board_cars() {
        System.out.println("BOARDING");
        update_status(BoatStatus.Boarding);
        wait_for_boarding_cars();

    }

    void arriveToHarbor() {
        Harbor harbor = harbors[destination];
        System.out.println("statek [ " + getId() + "] doplynal do  portu " + harbor.Name);
        status =  BoatStatus.Waiting;

        harbor.lock.lock();
        System.out.println("PORT [" + harbor.Name + "]zaczyna zaladowywać statek [ " + getId()+ "]");

        harbor.currentBoat = this;
        harbor.semaphore.release(capacity_);
        release_cars();
        board_cars();
        status = BoatStatus.Moving;
        System.out.println("PORT [" + harbor.Name + "] skonczyl zaladowywać statek [ " + getId()+ "]");
        harbor.currentBoat = null;
        status = BoatStatus.Moving;
        harbor.lock.unlock();
    }

    public void run() {
        while (true) {
            travel();
            arriveToHarbor();
        }
    }
}
