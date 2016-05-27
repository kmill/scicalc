package scicalc;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		Interpreter interp = new Interpreter();
		try {
			System.out.println(interp.evaluate("1+2*3"));
			System.out.println(interp.evaluate("null"));
			System.out.println(interp.evaluate("if false then 1 else 2"));
			System.out.println(interp.evaluate("(fun (x) -> x+ 1)(22)"));
			System.out.println(interp.evaluate("fib := fun (n) -> if n <= 1 then n else fib(n-1) + fib(n-2)"));
			System.out.println(interp.evaluate("fib(10)"));

			String testFile = new String(Files.readAllBytes(Paths.get("test.sci")), StandardCharsets.UTF_8);
			System.out.println(interp.evaluate(testFile));
		} catch (Exception x) {
			System.err.println(x.toString());
		}
	}
}
