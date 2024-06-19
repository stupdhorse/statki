package PW;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Harbor {

    public Harbor(String name) {
        maxCapacity = 10;
        Name = name;
    }

    String Name;
    int maxCapacity;
    Lock lock = new ReentrantLock(true);
    Semaphore semaphore = new Semaphore(0,true);
    Boat currentBoat;

}