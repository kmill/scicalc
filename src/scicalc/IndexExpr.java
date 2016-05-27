package scicalc;

/**
 * Array indexing
 * 
 * @author kmill
 *
 */
public class IndexExpr implements Expr {
	private Expr m_array, m_index;

	public IndexExpr(Expr array, Expr index) {
		m_array = array;
		m_index = index;
	}

	@Override
	public Value evaluate(Environment e) {
		Value array = m_array.evaluate(e);
		Value index = m_index.evaluate(e);
		return array.get(index);
	}

	public String toString() {
		return "Index(" + m_array + ", " + m_index + ")";
	}

}
