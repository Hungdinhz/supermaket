package command;

interface Command { void execute(); }

class Light {
    void turnOn() { System.out.println(" -> [Den phong khach] Da BAT sang."); }
    void turnOff() { System.out.println(" -> [Den phong khach] Da TAT."); }
}

class TurnOnCommand implements Command {
    private final Light light;
    TurnOnCommand(Light light) { this.light = light; }
    public void execute() { light.turnOn(); }
}

class TurnOffCommand implements Command {
    private final Light light;
    TurnOffCommand(Light light) { this.light = light; }
    public void execute() { light.turnOff(); }
}

class RemoteControl {
    private Command command;
    void setCommand(Command command) { this.command = command; }
    void pressButton() {
        if (command == null) throw new IllegalStateException("Chua gan lenh cho nut bam!");
        System.out.println("Nhan nut tren dieu khien...");
        command.execute();
    }
}

public class Main {
    public static void main(String[] args) {
        Light light = new Light();
        RemoteControl remote = new RemoteControl();

        System.out.println("Thao tac 1: Gan lenh Bat den va nhan nut");
        remote.setCommand(new TurnOnCommand(light));
        remote.pressButton();
        System.out.println("----------");

        System.out.println("Thao tac 2: Gan lenh Tat den va nhan nut");
        remote.setCommand(new TurnOffCommand(light));
        remote.pressButton();
        System.out.println("----------");

        System.out.println("Thao tac 3: Bat lai den");
        remote.setCommand(new TurnOnCommand(light));
        remote.pressButton();
    }
}
