package scicalc;

import java.util.ArrayList;

// An ArrayExpr is an expression which evaluates to an ArrayValue, which is a wrapper around ArrayList<Value>.
public class ArrayExpr implements Expr {
	private Expr[] m_items;

	public ArrayExpr(Expr[] items) {
		m_items = items;
	}

	@Override
	public Value evaluate(Environment e) {
		ArrayList<Value> items = new ArrayList<>();
		for (Expr expr : m_items) {
			items.add(expr.evaluate(e));
		}
		return new ArrayValue(items);
	}

	public String toString() {
		StringBuffer args = new StringBuffer();
		String delim = "";
		for (int i = 0; i < m_items.length; i++) {
			args.append(delim);
			delim = ", ";
			args.append(m_items[i].toString());
		}
		return "[" + args + "]";
	}
}
