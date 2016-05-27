package scicalc;

public class VariableExpr implements Expr {
	private String m_identifier;

	public VariableExpr(String identifier) {
		m_identifier = identifier;
	}

	public String getIdentifier() {
		return m_identifier;
	}

	@Override
	public Value evaluate(Environment e) {
		return e.lookup(m_identifier);
	}

	public String toString() {
		return m_identifier;
	}
}
