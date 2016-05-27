package scicalc;

public interface Value {
	public double asFloat();
	public boolean asBool();
	public Value apply(Value[] values);
	public Value get(Value index);
	public Value set(Value index, Value value);
}
