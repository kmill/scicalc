package scicalc;

/**
 * 
 * @author kmill
 *
 */
public class NullValue implements Expr, Value {

	// This is a singleton (i.e., only one instance of NullValue exists)
	public static NullValue INSTANCE = new NullValue();

	private NullValue() {
	}

	@Override
	public Value evaluate(Environment e) {
		return this;
	}

	@Override
	public double asFloat() {
		return 0.0; // maybe a good idea, maybe not?
	}
	
	@Override
	public boolean asBool() {
		return false;
	}

	@Override
	public Value apply(Value[] values) {
		throw new IllegalArgumentException("Null is not a function");
	}
	
	@Override
	public Value get(Value index) {
		throw new IllegalArgumentException("Null is not indexable");
	}
	
	@Override
    public Value set(Value index, Value value) {
        throw new IllegalArgumentException("Null is not indexable");
    }

	public String toString() {
		return "null";
	}

}
