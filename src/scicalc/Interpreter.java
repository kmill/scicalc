package scicalc;

public class Interpreter {
	private Environment m_env;

	public Interpreter() {
		m_env = new Environment();

		m_env.set("PI", Math.PI);
		m_env.set("E", Math.E);
		m_env.set("print", PrimFuncValue.PRINT);
		m_env.set("print_line", PrimFuncValue.PRINT_LINE);
	}

	public Value evaluate(String input) throws Exception {
		return new SciParser(new SciScanner(input)).parseTopExpr().evaluate(m_env);
	}
}
