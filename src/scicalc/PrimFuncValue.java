package scicalc;

import java.util.ArrayList;

public enum PrimFuncValue implements Expr,Value {

    // arithmetic
    ADD2, SUB2, MUL2, DIV2, IDIV2, MOD2, POW2, NEG1, PLUS1,
    // comparisons
    LT2, LTE2, GT2, GTE2, EQ2, NEQ2,
    // output
    PRINT, PRINT_LINE,
    // arrays
    MAKE_ARRAY, GET, SET, PUSH, POP, EXTEND, LEN;

    @Override
    public Value evaluate(Environment e) {
        return this;
    }

    @Override
    public double asFloat() {
        throw new IllegalArgumentException("Function is not a float");
    }

    @Override
    public boolean asBool() {
        return true; // like in Python
    }

    private void checkNumArgs(Value[] values, int expected) {
        if (values.length != expected) {
            throw new IllegalArgumentException("Expecting " + expected + " arguments.  Given " + values.length);
        }
    }

    @Override
    public Value apply(Value[] values) {
        switch (this) {
        case ADD2:
            checkNumArgs(values, 2);
            return new FloatValue(values[0].asFloat() + values[1].asFloat());
        case SUB2:
            checkNumArgs(values, 2);
            return new FloatValue(values[0].asFloat() - values[1].asFloat());
        case MUL2:
            checkNumArgs(values, 2);
            return new FloatValue(values[0].asFloat() * values[1].asFloat());
        case DIV2:
            checkNumArgs(values, 2);
            return new FloatValue(values[0].asFloat() / values[1].asFloat());
        case IDIV2:
            checkNumArgs(values, 2);
            return new FloatValue((int) values[0].asFloat() / (int) values[1].asFloat());
        case MOD2:
            checkNumArgs(values, 2);
            return new FloatValue(values[0].asFloat() % values[1].asFloat());
        case POW2:
            checkNumArgs(values, 2);
            return new FloatValue(Math.pow(values[0].asFloat(), values[1].asFloat()));
        case NEG1:
            checkNumArgs(values, 1);
            return new FloatValue(-values[0].asFloat());
        case PLUS1:
            checkNumArgs(values, 1);
            return new FloatValue(values[0].asFloat());

        case LT2:
            checkNumArgs(values, 2);
            return BoolValue.create(values[0].asFloat() < values[1].asFloat());
        case LTE2:
            checkNumArgs(values, 2);
            return BoolValue.create(values[0].asFloat() <= values[1].asFloat());
        case GT2:
            checkNumArgs(values, 2);
            return BoolValue.create(values[0].asFloat() > values[1].asFloat());
        case GTE2:
            checkNumArgs(values, 2);
            return BoolValue.create(values[0].asFloat() >= values[1].asFloat());
        case EQ2:
            checkNumArgs(values, 2);
            return BoolValue.create(values[0].equals(values[1]));
        case NEQ2:
            checkNumArgs(values, 2);
            return BoolValue.create(values[0].asFloat() != values[1].asFloat());

        case PRINT:
            for (int i = 0; i < values.length; i++) {
                if (i > 0)
                    System.out.print(" ");
                System.out.print(values[i]);
            }
            return NullValue.INSTANCE;
        case PRINT_LINE:
            for (int i = 0; i < values.length; i++) {
                if (i > 0)
                    System.out.print(" ");
                System.out.print(values[i]);
            }
            System.out.println();
            return NullValue.INSTANCE;

        case MAKE_ARRAY: {
            // make_array() creates an array of length 0
            // make_array(n) creates an array of length n filled with null
            // make_array(n, v) creates an array of length n filled with v
            int length = 0;
            if (values.length > 0) {
                length = (int) values[0].asFloat();
                if (length != values[0].asFloat()) {
                    throw new IllegalArgumentException("Length must be a float");
                }
            }
            Value fill = NullValue.INSTANCE;
            if (values.length > 1) {
                fill = values[1];
            }
            if (values.length > 2) {
                throw new IllegalArgumentException("Expecting at most two arguments");
            }
            ArrayList<Value> items = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                items.add(fill);
            }
            return new ArrayValue(items);
        }
        case GET:
            checkNumArgs(values, 2);
            return values[0].get(values[1]);
        case SET:
            checkNumArgs(values, 3);
            values[0].set(values[1], values[2]);
            return NullValue.INSTANCE;
        case PUSH:
            checkNumArgs(values, 2);
            if (values[0] instanceof ArrayValue) {
                ((ArrayValue) values[0]).getArray().add(values[1]);
                return NullValue.INSTANCE;
            } else {
                throw new IllegalArgumentException("First argument must be an array");
            }
        case POP:
            checkNumArgs(values, 2);
            if (values[0] instanceof ArrayValue) {
                ArrayList<Value> array = ((ArrayValue) values[0]).getArray();
                return array.remove(array.size() - 1);
            } else {
                throw new IllegalArgumentException("First argument must be an array");
            }
        case EXTEND:
            checkNumArgs(values, 2);
            if (values[0] instanceof ArrayValue && values[1] instanceof ArrayValue) {
                ArrayList<Value> array1 = ((ArrayValue) values[0]).getArray();
                ArrayList<Value> array2 = ((ArrayValue) values[1]).getArray();
                array1.addAll(array2);
                return NullValue.INSTANCE;
            } else {
                throw new IllegalArgumentException("Both arguments must be arrays");
            }
        case LEN:
            checkNumArgs(values, 1);
            if (values[0] instanceof ArrayValue) {
                return new FloatValue(((ArrayValue) values[0]).getArray().size());
            } else if (values[0] instanceof StringValue) {
                return new FloatValue(((StringValue) values[0]).getString().length());
            } else {
                throw new IllegalArgumentException();
            }
        }
        throw new InternalError("missing Func implementation");
    }

    @Override
    public Value get(Value index) {
        throw new IllegalArgumentException("Function is not indexable");
    }

    @Override
    public Value set(Value index, Value value) {
        throw new IllegalArgumentException("Function is not indexable");
    }
}
