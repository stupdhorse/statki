package PW;

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
        //releasingBarrier = null;
        countDownLatch = new CountDownLatch(capacity);
    }

    int capacity_;
    int distance_;
    int velocity_;
    int destination;
    //TimedSemaphore boardingSemaphore = new Semaphore(capacity_);
    Semaphore semaphore;
    BoatStatus status;

    //CyclicBarrier releasingBarrier;

    final Lock lock = new ReentrantLock(true);
    final Condition statusCondition = lock.newCondition();
    final CountDownLatch countDownLatch;
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

    void board_cars() {
        System.out.println("BOARDING");
        update_status(BoatStatus.Boarding);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }




    void arriveToHarbor() {
        Harbor harbor = harbors[destination];
        System.out.println("statek [ " + getId() + "] doplynal do  portu " + harbor.Name);
        status =  BoatStatus.Waiting;

        harbor.lock.lock();
        System.out.println("PORT [" + harbor.Name + "]zaczyna zaladowywać statek [ " + getId()+ "]");

        harbor.currentBoat = this;
        harbor.semaphore.release(capacity_);
        //REMOVE OLD CARS
        release_cars();
        //czekamy na auta jak zjada




        //ADD NEW CARS
        board_cars();
        status = BoatStatus.Moving;
        try {
            Thread.sleep(5000);
        } catch(InterruptedException e) {
            throw  new RuntimeException(e);
        }
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
