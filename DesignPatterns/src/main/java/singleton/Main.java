package singleton;

class AppConfig {
    private static final AppConfig INSTANCE = new AppConfig();
    private final String appName = "MiniShop E-Commerce v1.0";

    private AppConfig() {
        System.out.println("[AppConfig] Khoi tao doi tuong cau hinh duy nhat trong he thong.");
    }

    static AppConfig getInstance() {
        return INSTANCE;
    }

    String getAppName() { return appName; }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Module 1 yeu cau lay cau hinh:");
        AppConfig config1 = AppConfig.getInstance();
        System.out.println("Ten ung dung (config1): " + config1.getAppName());
        System.out.println("----------");

        System.out.println("Module 2 yeu cau lay cau hinh:");
        AppConfig config2 = AppConfig.getInstance();
        System.out.println("Ten ung dung (config2): " + config2.getAppName());
        System.out.println("----------");

        System.out.println("Kiem tra tinh duy nhat (config1 == config2): " + (config1 == config2));
    }
}
