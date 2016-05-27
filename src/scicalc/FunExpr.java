package scicalc;

public class FunExpr implements Expr {

	private String[] m_parameters;
	private Expr m_body;

	public FunExpr(String[] parameters, Expr body) {
		m_parameters = parameters;
		m_body = body;
	}

	public String[] getParameters() {
		return m_parameters;
	}

	public Expr getBody() {
		return m_body;
	}

	@Override
	public Value evaluate(Environment e) {
		return new FunValue(e, this);
	}

	public String toString() {
		StringBuffer params = new StringBuffer();
		String delim = "";
		for (int i = 0; i < m_parameters.length; i++) {
			params.append(delim);
			delim = ", ";
			params.append(m_parameters[i].toString());
		}
		return "fun (" + params + ") begin " + m_body + " end";
	}
}
