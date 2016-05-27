package scicalc;

import java.util.ArrayList;

public class ArrayValue implements Value {

    private ArrayList<Value> m_items;

    public ArrayValue(ArrayList<Value> items) {
        m_items = items;
    }

    @Override
    public double asFloat() {
        throw new IllegalArgumentException("Array is not a number");
    }

    @Override
    public boolean asBool() {
        return m_items.size() > 0; // like in Python
    }

    @Override
    public Value apply(Value[] values) {
        throw new IllegalArgumentException("Array is not a function");
    }

    @Override
    public Value get(Value index) {
        double i = index.asFloat();
        if ((int) i != i) {
            throw new IllegalArgumentException("Index to array must be an integer");
        }
        return m_items.get((int) i);
    }

    @Override
    public Value set(Value index, Value value) {
        double i = index.asFloat();
        if ((int) i != i) {
            throw new IllegalArgumentException("Index to array must be an integer");
        }
        m_items.set((int) i, value);
        return NullValue.INSTANCE;
    }

    public ArrayList<Value> getArray() {
        return m_items;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        String delim = "";
        for (Value item : m_items) {
            sb.append(delim);
            delim = ", ";
            sb.append(item);
        }
        return "[" + sb.toString() + "]";
    }

}
