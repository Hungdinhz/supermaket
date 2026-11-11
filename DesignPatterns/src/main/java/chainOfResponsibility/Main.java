package chainOfResponsibility;

abstract class SupportHandler {
    protected SupportHandler next;

    SupportHandler setNext(SupportHandler next) {
        this.next = next;
        return next;
    }

    abstract void handle(String request);

    protected void forward(String request) {
        if (next != null) {
            next.handle(request);
        } else {
            System.out.println(" -> [He thong] Khong co bo phan nao xu ly duoc yeu cau: \"" + request + "\"");
        }
    }
}

class BasicHandler extends SupportHandler {
    void handle(String request) {
        if (request.equalsIgnoreCase("mat mat khau")) {
            System.out.println(" -> [Cap 1 - Nhan vien CSKH] Da xu ly yeu cau: " + request);
        } else {
            System.out.println(" [Cap 1 - CSKH] Vuot qua tham quyen, chuyen tiep len Cap Ky thuat...");
            forward(request);
        }
    }
}

class TechnicalHandler extends SupportHandler {
    void handle(String request) {
        if (request.equalsIgnoreCase("loi may chu")) {
            System.out.println(" -> [Cap 2 - Ky thuat vien] Da khac phuc su co: " + request);
        } else {
            System.out.println(" [Cap 2 - Ky thuat] Vuot qua tham quyen, chuyen tiep len Quan ly...");
            forward(request);
        }
    }
}

class ManagerHandler extends SupportHandler {
    void handle(String request) {
        if (request.equalsIgnoreCase("hoan tien")) {
            System.out.println(" -> [Cap 3 - Quan ly] Da phe duyet yeu cau: " + request);
        } else {
            System.out.println(" [Cap 3 - Quan ly] Khong the giai quyet yeu cau nay.");
            forward(request);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        SupportHandler basic = new BasicHandler();
        basic.setNext(new TechnicalHandler()).setNext(new ManagerHandler());

        System.out.println("Yeu cau 1: Khach hang quen mat khau");
        basic.handle("mat mat khau");
        System.out.println("----------");

        System.out.println("Yeu cau 2: He thong bao loi 500");
        basic.handle("loi may chu");
        System.out.println("----------");

        System.out.println("Yeu cau 3: Khach hang yeu cau boi thuong");
        basic.handle("hoan tien");
        System.out.println("----------");

        System.out.println("Yeu cau 4: Yeu cau ngoai danh muc");
        basic.handle("mua co phan cong ty");
    }
}
