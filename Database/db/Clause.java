package db;

/**
 * Created by Kevin on 2017-03-04.
 */
public class Clause {
    String operator;
    int operand1;
    int operand2;
    Value operand3;
    boolean unary;

    Clause(int a) {
        operand1 = a;
        operand2 = -1;
        operator = null;
    }

    Clause(int a, int b, String s) {
        operator = s;
        operand1 = a;
        operand2 = b;
        unary = false;
    }

    Clause(int a, Value b, String s, boolean u) {
        operator = s;
        operand1 = a;
        operand3 = b;
        unary = u;
    }
}
