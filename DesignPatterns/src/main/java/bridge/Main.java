package bridge;

interface Device {
    void turnOn();
    void setVolume(int level);
}

class TV implements Device {
    public void turnOn() { System.out.println("TV thong minh: Da khoi dong man hinh."); }
    public void setVolume(int level) { System.out.println("TV thong minh: Dat am luong o muc " + level + "."); }
}

class Radio implements Device {
    public void turnOn() { System.out.println("Radio FM: Da bat song phat thanh."); }
    public void setVolume(int level) { System.out.println("Radio FM: Dat am luong o muc " + level + "."); }
}

class RemoteControl {
    protected final Device device;
    RemoteControl(Device device) { this.device = device; }

    void power() {
        System.out.println("Nhan nut Nguon tren dieu khien...");
        device.turnOn();
    }
    void volumeUp() {
        System.out.println("Nhan nut Tang am luong tren dieu khien...");
        device.setVolume(15);
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Ket noi dieu khien voi TV:");
        RemoteControl tvRemote = new RemoteControl(new TV());
        tvRemote.power();
        tvRemote.volumeUp();
        System.out.println("----------");

        System.out.println("Ket noi dieu khien voi Radio:");
        RemoteControl radioRemote = new RemoteControl(new Radio());
        radioRemote.power();
        radioRemote.volumeUp();
    }
}
