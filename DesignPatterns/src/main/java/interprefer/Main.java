package interprefer;

interface Expression {
    int interpret();
}

class NumberExpression implements Expression {
    private final int value;
    NumberExpression(int value) { this.value = value; }
    public int interpret() { return value; }
}

class AddExpression implements Expression {
    private final Expression left, right;
    AddExpression(Expression left, Expression right) {
        this.left = left; this.right = right;
    }
    public int interpret() { return left.interpret() + right.interpret(); }
}

class SubtractExpression implements Expression {
    private final Expression left, right;
    SubtractExpression(Expression left, Expression right) {
        this.left = left; this.right = right;
    }
    public int interpret() { return left.interpret() - right.interpret(); }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Bieu thuc 1: (5 + 3) - 2");
        Expression expr1 = new SubtractExpression(
                new AddExpression(new NumberExpression(5), new NumberExpression(3)),
                new NumberExpression(2));
        System.out.println(" -> Ket qua thong dich: " + expr1.interpret());
        System.out.println("----------");

        System.out.println("Bieu thuc 2: (20 - 5) + (10 - 3)");
        Expression expr2 = new AddExpression(
                new SubtractExpression(new NumberExpression(20), new NumberExpression(5)),
                new SubtractExpression(new NumberExpression(10), new NumberExpression(3)));
        System.out.println(" -> Ket qua thong dich: " + expr2.interpret());
    }
}
