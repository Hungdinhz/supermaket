package visitor;

interface ShapeVisitor {
    void visitCircle(Circle circle);
    void visitRectangle(Rectangle rectangle);
}

interface Shape {
    void accept(ShapeVisitor visitor);
}

class Circle implements Shape {
    private final double radius;
    Circle(double radius) { this.radius = radius; }
    double getRadius() { return radius; }
    public void accept(ShapeVisitor visitor) { visitor.visitCircle(this); }
}

class Rectangle implements Shape {
    private final double width, height;
    Rectangle(double width, double height) {
        this.width = width; this.height = height;
    }
    double getWidth() { return width; }
    double getHeight() { return height; }
    public void accept(ShapeVisitor visitor) { visitor.visitRectangle(this); }
}

class AreaVisitor implements ShapeVisitor {
    public void visitCircle(Circle circle) {
        double area = Math.PI * circle.getRadius() * circle.getRadius();
        System.out.printf(" -> Hinh tron (ban kinh r = %.1f) | Dien tich = %.2f%n",
                circle.getRadius(), area);
    }

    public void visitRectangle(Rectangle rectangle) {
        double area = rectangle.getWidth() * rectangle.getHeight();
        System.out.printf(" -> Hinh chu nhat (rong = %.1f, cao = %.1f) | Dien tich = %.2f%n",
                rectangle.getWidth(), rectangle.getHeight(), area);
    }
}

public class Main {
    public static void main(String[] args) {
        ShapeVisitor areaVisitor = new AreaVisitor();

        System.out.println("Tap hop hinh hoc 1:");
        Shape[] group1 = { new Circle(2.0), new Rectangle(3.0, 4.0) };
        for (Shape shape : group1) {
            shape.accept(areaVisitor);
        }
        System.out.println("----------");

        System.out.println("Tap hop hinh hoc 2 (Kich thuoc lon hon):");
        Shape[] group2 = { new Circle(5.5), new Rectangle(10.0, 6.5), new Circle(3.2) };
        for (Shape shape : group2) {
            shape.accept(areaVisitor);
        }
    }
}
