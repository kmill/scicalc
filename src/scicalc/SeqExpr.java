package scicalc;

/**
 * A sequence expression evaluates the first thing then the second thing. The
 * second thing's evaluated value is the value for the whole sequence
 * expression.  In the language, sequences are represented by semicolons.
 * 
 * @author kmill
 *
 */
public class SeqExpr implements Expr {
	private Expr m_a, m_b;

	public SeqExpr(Expr a, Expr b) {
		m_a = a;
		m_b = b;
	}

	@Override
	public Value evaluate(Environment e) {
		m_a.evaluate(e);
		return m_b.evaluate(e);
	}

	public String toString() {
		return "Seq(" + m_a + ", " + m_b + ")";
	}

}
