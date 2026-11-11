package strategy;

interface ShippingStrategy {
    double calculateFee(double distanceKm);
}

class StandardShipping implements ShippingStrategy {
    public double calculateFee(double distanceKm) {
        return 10000 + distanceKm * 2000;
    }
}

class ExpressShipping implements ShippingStrategy {
    public double calculateFee(double distanceKm) {
        return 20000 + distanceKm * 3500;
    }
}

class ShippingCalculator {
    private ShippingStrategy strategy;

    ShippingCalculator(ShippingStrategy strategy) { this.strategy = strategy; }
    void setStrategy(ShippingStrategy strategy) { this.strategy = strategy; }

    double calculate(double distanceKm) {
        return strategy.calculateFee(distanceKm);
    }
}

public class Main {
    public static void main(String[] args) {
        ShippingCalculator calculator = new ShippingCalculator(new StandardShipping());

        System.out.println("Don hang 1: Khoang cach 5 km");
        System.out.println(" -> Phi giao hang Tieu chuan: " + calculator.calculate(5) + " VND");
        calculator.setStrategy(new ExpressShipping());
        System.out.println(" -> Phi giao hang Hoa toc: " + calculator.calculate(5) + " VND");
        System.out.println("----------");

        System.out.println("Don hang 2: Khoang cach 15.5 km");
        calculator.setStrategy(new StandardShipping());
        System.out.println(" -> Phi giao hang Tieu chuan: " + calculator.calculate(15.5) + " VND");
        calculator.setStrategy(new ExpressShipping());
        System.out.println(" -> Phi giao hang Hoa toc: " + calculator.calculate(15.5) + " VND");
    }
}
