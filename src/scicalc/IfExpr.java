package scicalc;

/**
 * An 'if' expression is like "cond?cons:alt" in Java or "cons if cond else alt"
 * in Python. Importantly, the result of an 'if' statement has a value, which is
 * the value of whichever of the consequent or alternate were evaluated. The
 * 'cond' condition is what chooses which of the two get evaluated.
 * 
 * @author kmill
 *
 */
public class IfExpr implements Expr {

	private Expr m_cond, m_cons, m_alt;

	public IfExpr(Expr cond, Expr cons, Expr alt) {
		m_cond = cond;
		m_cons = cons;
		m_alt = alt;
	}

	@Override
	public Value evaluate(Environment e) {
		if (m_cond.evaluate(e).asBool()) {
			return m_cons.evaluate(e);
		} else {
			return m_alt.evaluate(e);
		}
	}

	public String toString() {
		return "if " + m_cond + " then " + m_cons + " else " + m_alt + " end";
	}
}
