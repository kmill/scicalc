package scicalc;

/**
 * Float values are "self-evaluating" in that they are already evaluated, so
 * when we evaluate them, we just return itself.
 * 
 * @author kmill
 *
 */
public class BoolValue implements Expr, Value {
	public static BoolValue TRUE_INSTANCE = new BoolValue(true), FALSE_INSTANCE = new BoolValue(false);

	private boolean m_val;

	private BoolValue(boolean val) {
		m_val = val;
	}

	public static BoolValue create(boolean val) {
		if (val) {
			return TRUE_INSTANCE;
		} else {
			return FALSE_INSTANCE;
		}
	}

	@Override
	public Value evaluate(Environment e) {
		return this;
	}

	@Override
	public double asFloat() {
		return m_val ? 1.0 : 0.0;
	}

	@Override
	public boolean asBool() {
		return m_val;
	}

	@Override
	public Value apply(Value[] values) {
		throw new IllegalArgumentException("Boolean is not a function");
	}

	@Override
	public Value get(Value index) {
		throw new IllegalArgumentException("Boolean is not indexable");
	}

	public String toString() {
		return Boolean.toString(m_val);
	}
}
