package abstractFactory;

interface Button { void render(); }
interface Checkbox { void render(); }

class LightButton implements Button {
    public void render() { System.out.println(" -> [Button] Hien thi Nut bam giao dien SANG (nen trang, chu den)."); }
}
class DarkButton implements Button {
    public void render() { System.out.println(" -> [Button] Hien thi Nut bam giao dien TOI (nen xam den, chu trang)."); }
}

class LightCheckbox implements Checkbox {
    public void render() { System.out.println(" -> [Checkbox] Hien thi O tich giao dien SANG (vien xanh nhat)."); }
}
class DarkCheckbox implements Checkbox {
    public void render() { System.out.println(" -> [Checkbox] Hien thi O tich giao dien TOI (vien neon)."); }
}

interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

class LightFactory implements GUIFactory {
    public Button createButton() { return new LightButton(); }
    public Checkbox createCheckbox() { return new LightCheckbox(); }
}

class DarkFactory implements GUIFactory {
    public Button createButton() { return new DarkButton(); }
    public Checkbox createCheckbox() { return new DarkCheckbox(); }
}

class Application {
    private final Button button;
    private final Checkbox checkbox;

    Application(GUIFactory factory) {
        button = factory.createButton();
        checkbox = factory.createCheckbox();
    }

    void render() {
        button.render();
        checkbox.render();
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Khoi tao ung dung voi che do Ban ngay (Light Theme):");
        Application lightApp = new Application(new LightFactory());
        lightApp.render();
        System.out.println("----------");

        System.out.println("Chuyen doi ung dung sang che do Ban dem (Dark Theme):");
        Application darkApp = new Application(new DarkFactory());
        darkApp.render();
    }
}
