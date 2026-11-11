package observer;

import java.util.ArrayList;
import java.util.List;

interface Observer {
    void update(double temperature);
}

class WeatherStation {
    private final List<Observer> observers = new ArrayList<>();
    private double temperature;

    void addObserver(Observer observer) {
        observers.add(observer);
        System.out.println("Da dang ky them 1 thiet bi nhan thong bao.");
    }

    void setTemperature(double temperature) {
        System.out.println("Tram thoi tiet cap nhat nhiet do moi: " + temperature + " C");
        this.temperature = temperature;
        for (Observer observer : observers) {
            observer.update(this.temperature);
        }
    }
}

class PhoneDisplay implements Observer {
    public void update(double temperature) {
        System.out.println(" -> [Ung dung Dien thoai] Thong bao day: Nhiet do hien tai la " + temperature + " C");
    }
}

class TVDisplay implements Observer {
    public void update(double temperature) {
        System.out.println(" -> [Man hinh TV] Cap nhat goc man hinh: Nhiet do " + temperature + " C");
    }
}

public class Main {
    public static void main(String[] args) {
        WeatherStation station = new WeatherStation();
        station.addObserver(new PhoneDisplay());
        station.addObserver(new TVDisplay());
        System.out.println("----------");

        System.out.println("Buoi sang:");
        station.setTemperature(25.0);
        System.out.println("----------");

        System.out.println("Buoi trua:");
        station.setTemperature(33.5);
        System.out.println("----------");

        System.out.println("Buoi toi:");
        station.setTemperature(27.2);
    }
}
