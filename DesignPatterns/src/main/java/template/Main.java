package template;

abstract class Beverage {
    public final void prepare() {
        boilWater();
        brew();
        pourInCup();
        addCondiments();
        System.out.println("=> Hoan tat quy trinh pha che!");
    }

    private void boilWater() { System.out.println("1. Dun soi nuoc o nhiet do 100 do C."); }
    protected abstract void brew();
    private void pourInCup() { System.out.println("3. Rot do uong ra coc su."); }
    protected abstract void addCondiments();
}

class Coffee extends Beverage {
    protected void brew() { System.out.println("2. Pha ca phe bang phin truyen thong."); }
    protected void addCondiments() { System.out.println("4. Them sua dac va khuay deu."); }
}

class Tea extends Beverage {
    protected void brew() { System.out.println("2. Ngam tui tra xanh trong 3 phut."); }
    protected void addCondiments() { System.out.println("4. Them lat chanh tuoi va mat ong."); }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Quy trinh pha Ca phe sua:");
        Beverage coffee = new Coffee();
        coffee.prepare();
        System.out.println("----------");

        System.out.println("Quy trinh pha Tra chanh:");
        Beverage tea = new Tea();
        tea.prepare();
    }
}
