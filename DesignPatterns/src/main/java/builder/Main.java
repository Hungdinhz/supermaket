package builder;

class Computer {
    private final String cpu;
    private final String ram;
    private final String storage;

    Computer(String cpu, String ram, String storage) {
        this.cpu = cpu;
        this.ram = ram;
        this.storage = storage;
    }

    public String toString() {
        return "Cau hinh May tinh [CPU=" + cpu + ", RAM=" + ram + ", O cung=" + storage + "]";
    }
}

class ComputerBuilder {
    private String cpu;
    private String ram = "8GB";
    private String storage = "256GB SSD";

    ComputerBuilder cpu(String value) { cpu = value; return this; }
    ComputerBuilder ram(String value) { ram = value; return this; }
    ComputerBuilder storage(String value) { storage = value; return this; }

    Computer build() {
        if (cpu == null || cpu.isBlank())
            throw new IllegalStateException("Loi: CPU khong duoc de trong khi lap rap may!");
        return new Computer(cpu, ram, storage);
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("1. Xay dung PC Gaming cao cap (tuy chinh toan bo):");
        Computer gamingPC = new ComputerBuilder()
                .cpu("Intel Core i9-14900K")
                .ram("64GB DDR5")
                .storage("2TB NVMe SSD")
                .build();
        System.out.println(" -> " + gamingPC);
        System.out.println("----------");

        System.out.println("2. Xay dung PC Van phong (dung RAM va SSD mac dinh):");
        Computer officePC = new ComputerBuilder()
                .cpu("Intel Core i3-12100")
                .build();
        System.out.println(" -> " + officePC);
        System.out.println("----------");

        System.out.println("3. Thu nghiem lap rap thieu CPU:");
        try {
            new ComputerBuilder().ram("16GB").build();
        } catch (IllegalStateException e) {
            System.out.println(" -> Bat ngoai le thanh cong: " + e.getMessage());
        }
    }
}
