package scicalc;

public class TestParser {

	public static void main(String[] args) {
		runParser("1 + 2");
		runParser("1 + 2 * 3");
		runParser("(1 + 2) * 3");
		runParser("-x+y");
		runParser("1+2-3+4-5");
		runParser("1*2+3*4+5*6");
		runParser("-1^2");
		runParser("2^3^4");
		runParser("2^-2"); // error
		runParser("2^(-2)"); // ok
		runParser("if n <= 1 do n else fib(n - 1) + fib(n - 2) end");
		runParser("if n<=0 do 0 elif n==1 do 1 else fib(n-1) + fib(n-2) end");
	}
	
	private static void runParser(String input) {
		SciParser p = new SciParser(new SciScanner(input));
		try {
			Expr e = p.parseTopExpr();
			System.out.println(e.toString());
		} catch (SciParser.ParseError x) {
			System.out.println(x.toString());
		}
	}

}
