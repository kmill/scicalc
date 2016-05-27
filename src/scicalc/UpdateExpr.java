package scicalc;

public class UpdateExpr implements Expr {

	private String m_identifier;
	private Expr m_expr;

	public UpdateExpr(String identifier, Expr expr) {
		m_identifier = identifier;
		m_expr = expr;
	}

	@Override
	public Value evaluate(Environment e) {
		e.update(m_identifier, m_expr.evaluate(e));
		return NullValue.INSTANCE;
	}

	public String toString() {
		return "Update(" + m_identifier + ", " + m_expr + ")";
	}
}
