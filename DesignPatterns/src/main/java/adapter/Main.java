package adapter;

interface PaymentProcessor {
    void pay(double amount);
}

class LegacyBank {
    void makePayment(int amount) {
        System.out.println("[LegacyBank] Kiem tra tai khoan va ket noi cong thanh toan cu...");
        System.out.println("[LegacyBank] Ngan hang xu ly thanh cong: " + amount + " VND");
    }
}

class BankAdapter implements PaymentProcessor {
    private final LegacyBank bank;

    BankAdapter(LegacyBank bank) {
        this.bank = bank;
    }

    @Override
    public void pay(double amount) {
        System.out.println("-> Yeu cau thanh toan tu ung dung: " + amount + " VND");
        int roundedAmount = (int) Math.round(amount);
        System.out.println("-> BankAdapter chuyen doi du lieu (double -> int): " + roundedAmount + " VND");
        bank.makePayment(roundedAmount);
    }
}

public class Main {
    public static void main(String[] args) {
        PaymentProcessor processor = new BankAdapter(new LegacyBank());

        System.out.println("Giao dich 1: Thanh toan don hang tieu chuan");
        processor.pay(150000.0);
        System.out.println("----------");

        System.out.println("Giao dich 2: Thanh toan don hang co so le thap phan");
        processor.pay(299499.75);
        System.out.println("----------");

        System.out.println("Giao dich 3: Thanh toan hoa don gia tri cao");
        processor.pay(1250000.40);
    }
}
