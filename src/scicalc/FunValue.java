package scicalc;

public class FunValue implements Value {

    private Environment m_env;
    private FunExpr m_fun;

    public FunValue(Environment env, FunExpr fun) {
        m_env = env;
        m_fun = fun;
    }

    @Override
    public double asFloat() {
        throw new IllegalArgumentException("Function cannot be float");
    }

    @Override
    public boolean asBool() {
        return true; // like in Python
    }

    @Override
    public Value apply(Value[] values) {
        if (values.length != m_fun.getParameters().length) {
            throw new IllegalArgumentException(
                    "Expecting " + m_fun.getParameters().length + " arguments but given " + values.length);
        }
        Environment env = new Environment(m_env);
        for (int i = 0; i < values.length; i++) {
            env.set(m_fun.getParameters()[i], values[i]);
        }
        return m_fun.getBody().evaluate(env);
    }

    @Override
    public Value get(Value index) {
        throw new IllegalArgumentException("Function is not indexable");
    }

    @Override
    public Value set(Value index, Value value) {
        throw new IllegalArgumentException("Function is not indexable");
    }

    public String toString() {
        return "<bound function " + super.toString() + ">";
    }

}
