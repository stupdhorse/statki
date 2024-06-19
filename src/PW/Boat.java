package PW;

import java.util.concurrent.BrokenBarrierException;
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
        releasingBarrier = null;
    }

    int capacity_;
    int distance_;
    int velocity_;
    int destination;
    //TimedSemaphore boardingSemaphore = new Semaphore(capacity_);
    Semaphore semaphore;
    BoatStatus status;

    CyclicBarrier releasingBarrier;

    Lock lock = new ReentrantLock(true);
    Condition condition = lock.newCondition();

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

    void release_cars() {
        System.out.println("REALEASING");
        status = BoatStatus.Releasing;
        if(releasingBarrier == null) {return;}
        try {
            releasingBarrier.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException ignored) {
        }
    }

    void board_cars() {
        System.out.println("BOARDING");
        releasingBarrier = new CyclicBarrier(capacity_);
        status = BoatStatus.Boarding;
        condition.notifyAll();

        try {
            releasingBarrier.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException ignored) {
        }

        System.out.println("boarding finishde");
        releasingBarrier = new CyclicBarrier(capacity_);
    }




    void arriveToHarbor() {
        Harbor harbor = harbors[destination];
        System.out.println("statek [ " + getId() + "] doplynal do  portu " + harbor.Name);
        status =  BoatStatus.Waiting;

        harbor.lock.lock();
        System.out.println("PORT [" + harbor.Name + "]zaczyna zaladowywać statek [ " + getId()+ "]");

        harbor.currentBoat = this;
        harbor.semaphore.release();
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
