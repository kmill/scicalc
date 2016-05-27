package scicalc;

public class WhileExpr implements Expr {
	private Expr m_cond, m_body;

	public WhileExpr(Expr cond, Expr body) {
		m_cond = cond;
		m_body = body;
	}

	@Override
	public Value evaluate(Environment e) {
		while (m_cond.evaluate(e).asBool()) {
			m_body.evaluate(e);
		}
		return NullValue.INSTANCE;
	}

	public String toString() {
		return "While(" + m_cond + ", " + m_body + ")";
	}
}
