package decorator;

interface Coffee {
    String getDescription();
    double cost();
}

class SimpleCoffee implements Coffee {
    public String getDescription() { return "Ca phe den nguyen chat"; }
    public double cost() { return 20000; }
}

abstract class CoffeeDecorator implements Coffee {
    protected final Coffee coffee;
    CoffeeDecorator(Coffee coffee) { this.coffee = coffee; }
}

class MilkDecorator extends CoffeeDecorator {
    MilkDecorator(Coffee coffee) { super(coffee); }
    public String getDescription() { return coffee.getDescription() + " + Sua tuoi"; }
    public double cost() { return coffee.cost() + 5000; }
}

class SugarDecorator extends CoffeeDecorator {
    SugarDecorator(Coffee coffee) { super(coffee); }
    public String getDescription() { return coffee.getDescription() + " + Duong"; }
    public double cost() { return coffee.cost() + 2000; }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Don hang 1: Ca phe co ban");
        Coffee order1 = new SimpleCoffee();
        System.out.println("Mo ta: " + order1.getDescription());
        System.out.println("Gia tien: " + order1.cost() + " VND");
        System.out.println("----------");

        System.out.println("Don hang 2: Ca phe them sua va duong");
        Coffee order2 = new SugarDecorator(new MilkDecorator(new SimpleCoffee()));
        System.out.println("Mo ta: " + order2.getDescription());
        System.out.println("Gia tien: " + order2.cost() + " VND");
        System.out.println("----------");

        System.out.println("Don hang 3: Ca phe gap doi sua (Double Milk)");
        Coffee order3 = new MilkDecorator(new MilkDecorator(new SimpleCoffee()));
        System.out.println("Mo ta: " + order3.getDescription());
        System.out.println("Gia tien: " + order3.cost() + " VND");
    }
}
