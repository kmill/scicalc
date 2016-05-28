package scicalc;

public class BlockExpr implements Expr {

    private String m_label;
    private Expr m_body;

    public BlockExpr(String label, Expr body) {
        m_label = label;
        m_body = body;
    }

    @Override
    public Value evaluate(Environment e) {
        LabelSecret secret = new LabelSecret();
        Environment e2 = new Environment(e);
        e2.set(m_label, new LabelValue(secret));
        try {
            return m_body.evaluate(e2);
        } catch (LabelSecret secret2) {
            if (secret == secret2) {
                return secret.getValue();
            } else {
                throw secret2;
            }
        } finally {
            secret.invalidate();
        }
    }

    public String toString() {
        return "Block(" + m_label + ", " + m_body + ")";
    }

    private class LabelSecret extends RuntimeException {
        private Value m_value;
        private boolean m_valid = true;

        public String getLabel() {
            return m_label;
        }

        public Value getValue() {
            return m_value;
        }

        public void setValue(Value value) {
            m_value = value;
        }
        
        public void invalidate() {
            m_valid = false;
        }
        public boolean isValid() {
            return m_valid;
        }
    }

    private static class LabelValue implements Value {
        private LabelSecret m_secret;

        public LabelValue(LabelSecret secret) {
            m_secret = secret;
        }

        @Override
        public double asFloat() {
            throw new IllegalArgumentException("Label is not a number");
        }

        @Override
        public boolean asBool() {
            return true;
        }

        @Override
        public Value apply(Value[] values) {
            if (!m_secret.isValid()) {
                throw new IllegalStateException("Label invalid outside defining block");
            }
            if (values.length == 0) {
                m_secret.setValue(NullValue.INSTANCE);
            } else if (values.length == 1) {
                m_secret.setValue(values[0]);
            } else {
                throw new IllegalArgumentException("Label expecting exactly one argument");
            }
            throw m_secret;
        }

        @Override
        public Value get(Value index) {
            throw new IllegalArgumentException("Label cannot be indexed");
        }

        @Override
        public Value set(Value index, Value value) {
            throw new IllegalArgumentException("Label cannot be indexed");
        }
    }
}
