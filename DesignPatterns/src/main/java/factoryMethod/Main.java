package factoryMethod;

interface Notification {
    void send(String message);
}

class EmailNotification implements Notification {
    public void send(String message) {
        System.out.println("[Kenh EMAIL] Dang ket noi may chu SMTP...");
        System.out.println("[Kenh EMAIL] Da gui thu voi noi dung: \"" + message + "\"");
    }
}

class SmsNotification implements Notification {
    public void send(String message) {
        System.out.println("[Kenh SMS] Dang ket noi nha mang vien thong...");
        System.out.println("[Kenh SMS] Da gui tin nhan: \"" + message + "\"");
    }
}

abstract class NotificationCreator {
    abstract Notification createNotification();

    void notifyUser(String message) {
        Notification notification = createNotification();
        notification.send(message);
    }
}

class EmailCreator extends NotificationCreator {
    Notification createNotification() { return new EmailNotification(); }
}

class SmsCreator extends NotificationCreator {
    Notification createNotification() { return new SmsNotification(); }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Kich ban 1: Thong bao xac nhan don hang qua Email");
        NotificationCreator creator = new EmailCreator();
        creator.notifyUser("Don hang #DH2026 cua ban da duoc xac nhan thanh cong.");
        System.out.println("----------");

        System.out.println("Kich ban 2: Gui ma OTP bao mat qua SMS");
        creator = new SmsCreator();
        creator.notifyUser("Ma xac thuc OTP cua ban la: 849201 (hieu luc 3 phut).");
    }
}
