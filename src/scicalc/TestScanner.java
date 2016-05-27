package scicalc;

public class TestScanner {
	public static void main(String[] args) {
		runScanner(new SciScanner("1 + 2"));
		runScanner(new SciScanner("(1+2)*3"));
		runScanner(new SciScanner("x+/*internal comment*/y/2.33"));
		runScanner(new SciScanner("/*unterminated comment"));
		runScanner(new SciScanner("if n <= 1 then n else fib(n - 1) + fib(n - 2) end"));
		runScanner(new SciScanner("(fun (x) begin x+ 1 end)(22)"));
	}

	private static void runScanner(SciScanner s) {
		while (true) {
			s.nextToken();
			switch (s.getTokenType()) {
			case ERROR:
				System.out.println("Error in scanner: " + s.getError());
				System.out.println(s.showLocation());
				return;
			case IDENTIFIER:
				System.out.println("IDENTIFIER: " + s.getToken());
				break;
			case RESERVED:
				System.out.println("RESERVED: " + s.getToken());
				break;
			case OPERATOR:
				System.out.println("OPERATOR: " + s.getToken());
				break;
			case FLOAT:
				System.out.println("FLOAT: " + s.getFloatVal());
				break;
			case EOF:
				System.out.println("EOF");
				return;
			}
		}
	}
}
