package scicalc;

/**
 * Function application.
 * @author kmill
 *
 */
public class AppExpr implements Expr {
	private Expr m_func;
	private Expr[] m_args;

	public AppExpr(Expr func, Expr[] args) {
		m_func = func;
		m_args = args;
	}

	public static AppExpr create(Expr func) {
		return new AppExpr(func, new Expr[0]);
	}

	public static AppExpr create(Expr func, Expr arg0) {
		return new AppExpr(func, new Expr[] { arg0 });
	}

	public static AppExpr create(Expr func, Expr arg0, Expr arg1) {
		return new AppExpr(func, new Expr[] { arg0, arg1 });
	}

	@Override
	public Value evaluate(Environment e) {
		Value func = m_func.evaluate(e);
		Value[] args = new Value[m_args.length];
		for (int i = 0; i < m_args.length; i++) {
			args[i] = m_args[i].evaluate(e);
		}
		return func.apply(args);
	}

	public String toString() {
		StringBuffer args = new StringBuffer();
		String delim = "";
		for (int i = 0; i < m_args.length; i++) {
			args.append(delim);
			delim = ", ";
			args.append(m_args[i].toString());
		}
		return "Apply(" + m_func + ", " + args.toString() + ")";
	}

}
