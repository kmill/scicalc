package scicalc;

public enum PrimFuncValue implements Expr, Value {

	ADD2, SUB2, MUL2, DIV2, MOD2, POW2, NEG1, PLUS1, LT2, LTE2, GT2, GTE2, EQ2, NEQ2, PRINT, PRINT_LINE;

	@Override
	public Value evaluate(Environment e) {
		return this;
	}

	@Override
	public double asFloat() {
		throw new IllegalArgumentException("Function is not a float");
	}

	@Override
	public boolean asBool() {
		return true; // like in Python
	}

	private void checkNumArgs(Value[] values, int expected) {
		if (values.length != expected) {
			throw new IllegalArgumentException("Expecting " + expected + " arguments.  Given " + values.length);
		}
	}

	@Override
	public Value apply(Value[] values) {
		switch (this) {
		case ADD2:
			checkNumArgs(values, 2);
			return new FloatValue(values[0].asFloat() + values[1].asFloat());
		case SUB2:
			checkNumArgs(values, 2);
			return new FloatValue(values[0].asFloat() - values[1].asFloat());
		case MUL2:
			checkNumArgs(values, 2);
			return new FloatValue(values[0].asFloat() * values[1].asFloat());
		case DIV2:
			checkNumArgs(values, 2);
			return new FloatValue(values[0].asFloat() / values[1].asFloat());
		case MOD2:
			checkNumArgs(values, 2);
			return new FloatValue(values[0].asFloat() % values[1].asFloat());
		case POW2:
			checkNumArgs(values, 2);
			return new FloatValue(Math.pow(values[0].asFloat(), values[1].asFloat()));
		case NEG1:
			checkNumArgs(values, 1);
			return new FloatValue(-values[0].asFloat());
		case PLUS1:
			checkNumArgs(values, 1);
			return new FloatValue(values[0].asFloat());
		case LT2:
			checkNumArgs(values, 2);
			return BoolValue.create(values[0].asFloat() < values[1].asFloat());
		case LTE2:
			checkNumArgs(values, 2);
			return BoolValue.create(values[0].asFloat() <= values[1].asFloat());
		case GT2:
			checkNumArgs(values, 2);
			return BoolValue.create(values[0].asFloat() > values[1].asFloat());
		case GTE2:
			checkNumArgs(values, 2);
			return BoolValue.create(values[0].asFloat() >= values[1].asFloat());
		case EQ2:
			// TODO make EQ test that things are of the same type, too?
			checkNumArgs(values, 2);
			return BoolValue.create(values[0].asFloat() == values[1].asFloat());
		case NEQ2:
			checkNumArgs(values, 2);
			return BoolValue.create(values[0].asFloat() != values[1].asFloat());

		case PRINT:
			checkNumArgs(values, 1);
			System.out.print(values[0] + " ");
			return NullValue.INSTANCE;
		case PRINT_LINE:
			checkNumArgs(values, 0);
			System.out.println();
			return NullValue.INSTANCE;
		default:
			throw new InternalError("missing Func implementation");
		}
	}

	@Override
	public Value get(Value index) {
		throw new IllegalArgumentException("Function is not indexable");
	}
}
