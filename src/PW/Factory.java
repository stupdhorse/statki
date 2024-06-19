package PW;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Factory {
    public static List<Boat> create_boats(int count,Harbor harborA, Harbor harborB) {
        List<Boat> boats = new ArrayList<Boat>(count);

        for(int i=0; i<count;i++){
            Boat boat = new Boat(10, harborA, harborB);
            boats.add(boat);
        }

        start_boats(boats);
        return boats;
    }

    public static void start_boats(List<Boat> boats) {
        for(var boat : boats) {
            boat.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static List<Car> create_cars(int count, List<Harbor> harbors) {

        List<Car> cars = new ArrayList<Car>(count * harbors.size());
        for(var harbor : harbors) {

            for(int i =0; i<count; i++) {
                Car car = new Car(harbor);
                cars.add(car);
            }
        }
        start_cars(cars);
        return cars;
    }

    public static void start_cars(List<Car> cars) {
        for(var car : cars) {
            car.start();
        }
    }
}
