package PW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class main {
    public static void main(String[] args) {

        int N = 2;
        int cars_count = 20;

        Harbor harborA = new Harbor("LUKOW");
        Harbor harborB = new Harbor("LODZ");

        List<Car> cars = Factory.create_cars(cars_count, new LinkedList<Harbor>(Arrays.asList(harborA, harborB)));
        List<Boat> boats = Factory.create_boats(N, harborA,harborB);
    }
}
