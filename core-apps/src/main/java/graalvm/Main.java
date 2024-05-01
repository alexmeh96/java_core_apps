package graalvm;

public class Main {
    public static void main(String[] args) {
        var car = new Car("BMW", 2010);
        System.out.println(car);
        car.setYear(2015);
        System.out.println(car.getYear());
    }
}
