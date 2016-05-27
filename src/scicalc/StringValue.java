package scicalc;

/**
 * String values are "self-evaluating" in that they are already evaluated, so
 * when we evaluate them, we just return itself.
 * 
 * @author kmill
 *
 */
public class StringValue implements Expr, Value {
    private String m_string;

    public StringValue(String string) {
        m_string = string;
    }

    public String getString() {
        return m_string;
    }

    @Override
    public Value evaluate(Environment e) {
        return this;
    }

    @Override
    public double asFloat() {
        return Double.parseDouble(m_string);
    }

    @Override
    public boolean asBool() {
        return m_string.length() != 0;
    }

    @Override
    public Value apply(Value[] values) {
        throw new IllegalArgumentException("String is not a function");
    }

    @Override
    public Value get(Value index) {
        double i = index.asFloat();
        if ((int) i != i) {
            throw new IllegalArgumentException("Index to string must be an integer");
        }
        return new FloatValue(m_string.charAt((int) i));
    }

    @Override
    public Value set(Value index, Value value) {
        throw new IllegalArgumentException("Strings are immutable");
    }

    public boolean equals(Object o) {
        if (o instanceof StringValue) {
            return this.m_string.equals(((StringValue) o).m_string);
        } else {
            return false;
        }
    }

    public String toString() {
        return m_string;
    }

}
