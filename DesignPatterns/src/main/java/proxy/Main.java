package proxy;

interface Image {
    void display();
}

class RealImage implements Image {
    private final String fileName;

    RealImage(String fileName) {
        this.fileName = fileName;
        loadFromDisk();
    }

    private void loadFromDisk() {
        System.out.println("[RealImage] Dang tai anh dung luong cao tu o cung: " + fileName + "...");
    }

    public void display() {
        System.out.println("[RealImage] Hien thi anh len man hinh: " + fileName);
    }
}

class ProxyImage implements Image {
    private final String fileName;
    private RealImage realImage;

    ProxyImage(String fileName) {
        this.fileName = fileName;
        System.out.println("[ProxyImage] Da khoi tao proxy cho '" + fileName + "' (chua tai anh that).");
    }

    public void display() {
        if (realImage == null) {
            System.out.println("[ProxyImage] Lan dau goi display() -> bat dau tai anh that:");
            realImage = new RealImage(fileName);
        } else {
            System.out.println("[ProxyImage] Anh da co san trong bo nho dem (cache), khong can tai lai.");
        }
        realImage.display();
    }
}

public class Main {
    public static void main(String[] args) {
        Image image = new ProxyImage("banner_quang_cao_4k.png");
        System.out.println("----------");

        System.out.println("Nguoi dung cuon trang den vi tri anh (Lan 1):");
        image.display();
        System.out.println("----------");

        System.out.println("Nguoi dung xem lai anh (Lan 2):");
        image.display();
    }
}
