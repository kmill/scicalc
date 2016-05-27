package scicalc;

/**
 * Float values are "self-evaluating" in that they are already evaluated, so
 * when we evaluate them, we just return itself.
 * 
 * @author kmill
 *
 */
public class FloatValue implements Expr, Value {
    private double m_number;

    public FloatValue(double number) {
        m_number = number;
    }

    @Override
    public Value evaluate(Environment e) {
        return this;
    }

    @Override
    public double asFloat() {
        return m_number;
    }

    @Override
    public boolean asBool() {
        return m_number != 0.0;
    }

    @Override
    public Value apply(Value[] values) {
        throw new IllegalArgumentException("Number is not a function");
    }

    @Override
    public Value get(Value index) {
        throw new IllegalArgumentException("Number is not indexable");
    }

    @Override
    public Value set(Value index, Value value) {
        throw new IllegalArgumentException("Number is not indexable");
    }

    public boolean equals(Object o) {
        if (o instanceof FloatValue) {
            FloatValue fo = (FloatValue) o;
            return this.m_number == fo.m_number;
        } else {
            return false;
        }
    }

    public String toString() {
        if (m_number == (int) m_number) {
            return Integer.toString((int) m_number);
        } else {
            return Double.toString(m_number);
        }
    }

}
