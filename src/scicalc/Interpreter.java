package scicalc;

public class Interpreter {
    private Environment m_env;

    public Interpreter() {
        m_env = new Environment();

        m_env.set("PI", Math.PI);
        m_env.set("E", Math.E);
        m_env.set("print", PrimFuncValue.PRINT);
        m_env.set("print_line", PrimFuncValue.PRINT_LINE);
        m_env.set("make_array", PrimFuncValue.MAKE_ARRAY);
        m_env.set("push", PrimFuncValue.PUSH);
        m_env.set("pop", PrimFuncValue.POP);
        m_env.set("extend", PrimFuncValue.EXTEND);
        m_env.set("len", PrimFuncValue.LEN);
    }

    public Value evaluate(String input) throws Exception {
        return new SciParser(new SciScanner(input)).parseTopExpr().evaluate(m_env);
    }
}
