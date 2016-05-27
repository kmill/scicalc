package scicalc;

public class OrExpr implements Expr {

	private Expr m_a, m_b;

	public OrExpr(Expr a, Expr b) {
		m_a = a;
		m_b = b;
	}

	@Override
	public Value evaluate(Environment e) {
		// we are taking advantage of Java's built-in short-circuiting || here
		return BoolValue.create(m_a.evaluate(e).asBool() || m_b.evaluate(e).asBool());
	}

	public String toString() {
		return "Or(" + m_a + ", " + m_b + ")";
	}

}
