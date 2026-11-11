package flighweight;

import java.util.HashMap;
import java.util.Map;

class TreeType {
    private final String name;
    private final String color;
    private final String texture;

    TreeType(String name, String color, String texture) {
        this.name = name;
        this.color = color;
        this.texture = texture;
        System.out.println("[TreeType] Khoi tao moi du lieu dung chung cho: " + name + " (" + color + ", " + texture + ")");
    }

    void draw(int x, int y) {
        System.out.println(" -> Ve '" + name + "' [mau " + color + ", " + texture + "] tai toa do (" + x + ", " + y + ")");
    }
}

class TreeFactory {
    private static final Map<String, TreeType> cache = new HashMap<>();

    static TreeType getType(String name, String color, String texture) {
        String key = name + "-" + color + "-" + texture;
        return cache.computeIfAbsent(key, k -> new TreeType(name, color, texture));
    }

    static int getTypeCount() { return cache.size(); }
}

class Tree {
    private final int x, y;
    private final TreeType type;

    Tree(int x, int y, TreeType type) {
        this.x = x; this.y = y; this.type = type;
    }

    void draw() { type.draw(x, y); }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Bat dau trong 5 cay trong cong vien:");
        TreeType oak = TreeFactory.getType("Cay soi", "xanh dam", "van go A");
        Tree t1 = new Tree(10, 20, oak);
        Tree t2 = new Tree(30, 40, oak);
        Tree t3 = new Tree(50, 60, TreeFactory.getType("Cay soi", "xanh dam", "van go A"));

        TreeType pine = TreeFactory.getType("Cay thong", "xanh la", "vo nham B");
        Tree t4 = new Tree(15, 75, pine);
        Tree t5 = new Tree(80, 90, pine);

        System.out.println("----------");
        System.out.println("Hien thi ban do cay:");
        t1.draw();
        t2.draw();
        t3.draw();
        t4.draw();
        t5.draw();

        System.out.println("----------");
        System.out.println("Tong so cay da ve: 5");
        System.out.println("So doi tuong TreeType thuc su duoc tao trong bo nho: " + TreeFactory.getTypeCount());
    }
}
