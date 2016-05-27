package scicalc;

public class StoreExpr implements Expr {

	private String m_identifier;
	private Expr m_expr;

	public StoreExpr(String identifier, Expr expr) {
		m_identifier = identifier;
		m_expr = expr;
	}

	@Override
	public Value evaluate(Environment e) {
		e.set(m_identifier, m_expr.evaluate(e));
		return NullValue.INSTANCE;
	}

	public String toString() {
		return "Store(" + m_identifier + ", " + m_expr + ")";
	}
}
