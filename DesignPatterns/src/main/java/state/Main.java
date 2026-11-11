package state;

interface VendingState {
    void insertMoney(VendingMachine machine);
    void selectItem(VendingMachine machine);
    void dispense(VendingMachine machine);
}

class VendingMachine {
    final VendingState noMoney = new NoMoneyState();
    final VendingState hasMoney = new HasMoneyState();
    final VendingState sold = new SoldState();
    private VendingState state = noMoney;

    void setState(VendingState state) { this.state = state; }
    void insertMoney() { state.insertMoney(this); }
    void selectItem() { state.selectItem(this); }
    void dispense() { state.dispense(this); }
}

class NoMoneyState implements VendingState {
    public void insertMoney(VendingMachine m) {
        System.out.println("Da nhan tien vao may.");
        m.setState(m.hasMoney);
    }
    public void selectItem(VendingMachine m) { System.out.println("Khong the chon san pham: Hay nap tien truoc!"); }
    public void dispense(VendingMachine m) { System.out.println("Khong the tra hang: Chua co san pham de tra!"); }
}

class HasMoneyState implements VendingState {
    public void insertMoney(VendingMachine m) { System.out.println("May da nhan tien roi, vui long chon san pham!"); }
    public void selectItem(VendingMachine m) {
        System.out.println("Da chon san pham thanh cong.");
        m.setState(m.sold);
    }
    public void dispense(VendingMachine m) { System.out.println("Vui long chon san pham truoc khi nhan hang!"); }
}

class SoldState implements VendingState {
    public void insertMoney(VendingMachine m) { System.out.println("Xin doi, may dang trong qua trinh tra san pham..."); }
    public void selectItem(VendingMachine m) { System.out.println("Dang tra san pham, khong the chon them!"); }
    public void dispense(VendingMachine m) {
        System.out.println("Da tra san pham. Cam on quy khach!");
        m.setState(m.noMoney);
    }
}

public class Main {
    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine();

        machine.selectItem();
        machine.insertMoney();
        machine.selectItem();
        machine.dispense();
        System.out.println("----------");

        machine.insertMoney();
        machine.insertMoney();
        machine.dispense();
        machine.selectItem();
        machine.dispense();
        System.out.println("----------");

        machine.dispense();
    }
}
