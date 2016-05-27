package scicalc;

import java.util.ArrayList;

/**
 * The Parser is responsible for taking textual input and determining its
 * structure. We could directly evaluate as we go, which is a reasonable way of
 * doing things, but sometime's it's nice to get ahold of the structure as a
 * bunch of objects.
 * 
 * This is called a "recursive descent" parser, or an LL(1) parser.
 * 
 * If we were just sticking with unary and binary operators, we could also use
 * the Shunting-yard algorithm, which is pretty nice for what it can do.
 * 
 * In the comments, square brackets parse [optionally], curly brackets parse
 * {zero or more times}, and vertical bar means parse (this|or|that).
 * Parentheses are just for (grouping). It's just a nice notation for quickly
 * describing a grammar. EBNF is similar but uses regular expression syntax.
 * 
 * @author kmill
 *
 */
public class SciParser {
	SciScanner m_scanner;

	public SciParser(SciScanner scanner) {
		m_scanner = scanner;
	}

	private Expr parseIf(Expr cond, Expr cons) throws ParseError {
		if (matchReserved("else")) {
			Expr alt = parseExpr();
			// checkToken(TokenType.RESERVED, "end");
			return new IfExpr(cond, cons, alt);
		} else if (matchReserved("elif")) {
			Expr cond2 = parseExpr();
			checkToken(TokenType.RESERVED, "then");
			Expr cons2 = parseExpr();
			return new IfExpr(cond, cons, parseIf(cond2, cons2));
		} else {
			// checkToken(TokenType.RESERVED, "end");
			return new IfExpr(cond, cons, NullValue.INSTANCE);
		}
	}

	private Expr parseTerm() throws ParseError {
		Expr e;
		if (m_scanner.isTokenType(TokenType.FLOAT)) {
			e = new FloatValue(m_scanner.getFloatVal());
			nextToken();
		} else if (matchOperator("(")) {
			// parse "(" expr {";"|expr} [";"] ")"
			// (a simpler form would have been "(" expr ")", but I wanted
			// sequencing somehow in the language)
			while (matchOperator(";")) {
				// consume leading semicolons
			}
			e = parseExpr();
			while (matchOperator(";")) {
				while (matchOperator(";")) {
					// consume semicolons
				}
				if (m_scanner.isTokenType(TokenType.OPERATOR) && m_scanner.isToken(")")) {
					break;
				}
				e = new SeqExpr(e, parseExpr());
			}
			checkToken(TokenType.OPERATOR, ")");
		} else if (matchOperator("[")) {
			ArrayList<Expr> items = new ArrayList<>();
			if (!matchOperator("]")) {
				items.add(parseExpr());
				while (matchOperator(",")) {
					items.add(parseExpr());
				}
				matchOperator(","); // allow trailing comma.
				checkToken(TokenType.OPERATOR, "]");
			}
			e = new ArrayExpr(items.toArray(new Expr[items.size()]));
		} else if (matchReserved("null")) {
			e = NullValue.INSTANCE;
		} else if (matchReserved("true")) {
			e = BoolValue.TRUE_INSTANCE;
		} else if (matchReserved("false")) {
			e = BoolValue.FALSE_INSTANCE;
		} else if (matchReserved("if")) {
			Expr cond = parseExpr();
			checkToken(TokenType.RESERVED, "then");
			Expr cons = parseExpr();
			e = parseIf(cond, cons);
		} else if (matchReserved("while")) {
			Expr cond = parseExpr();
			Expr body;
			if (matchOperator("->")) {
				body = parseExpr();
			} else {
				body = NullValue.INSTANCE;
			}
			e = new WhileExpr(cond, body);
		} else if (matchReserved("fun")) {
			checkToken(TokenType.OPERATOR, "(");
			ArrayList<String> parameters = new ArrayList<>();
			if (!matchOperator(")")) {
				if (m_scanner.isTokenType(TokenType.IDENTIFIER)) {
					parameters.add(m_scanner.getToken());
					nextToken();
				} else {
					throw parseError("Expecting parameter");
				}
				while (matchOperator(",")) {
					if (m_scanner.isTokenType(TokenType.IDENTIFIER)) {
						parameters.add(m_scanner.getToken());
						nextToken();
					} else {
						throw parseError("Expecting parameter");
					}
				}
				checkToken(TokenType.OPERATOR, ")");
			}
			checkToken(TokenType.OPERATOR, "->");
			Expr body = parseExpr();
			e = new FunExpr(parameters.toArray(new String[parameters.size()]), body);
		} else if (m_scanner.isTokenType(TokenType.IDENTIFIER)) {
			e = new VariableExpr(m_scanner.getToken());
			nextToken();
		} else {
			throw parseError("Expecting expression");
		}
		// now match parentheses for function application and brackets for
		// indexing
		while (true) {
			if (matchOperator("(")) {
				ArrayList<Expr> args = new ArrayList<>();
				if (!matchOperator(")")) {
					args.add(parseExpr());
					while (matchOperator(",")) {
						args.add(parseExpr());
					}
					matchOperator(","); // allow a trailing comma.
					checkToken(TokenType.OPERATOR, ")");
				}
				e = new AppExpr(e, args.toArray(new Expr[args.size()]));
			} else if (matchOperator("[")) {
				Expr index = parseExpr();
				checkToken(TokenType.OPERATOR, "]");
				e = new IndexExpr(e, index);
			} else {
				break;
			}
		}
		return e;
	}

	/**
	 * Parse term {"^" term}
	 * 
	 * @return
	 * @throws ParseError
	 */
	private Expr parsePow() throws ParseError {
		Expr expr = parseTerm();
		if (m_scanner.isTokenType(TokenType.OPERATOR) && m_scanner.isToken("^")) {
			// this is a little less straightforward than it could be because I
			// want to avoid allocating an ArrayList for every term whether or
			// not "^" appears.
			ArrayList<Expr> exprs = new ArrayList<>();
			exprs.add(expr);
			while (matchOperator("^")) {
				exprs.add(parseTerm());
			}
			// right-associativity means starting from the end and creating the
			// POW applications
			expr = exprs.get(exprs.size() - 1);
			for (int i = exprs.size() - 2; i >= 0; i--) {
				expr = AppExpr.create(PrimFuncValue.POW2, exprs.get(i), expr);
			}
		}
		return expr;
	}

	/**
	 * Parse pow {("*"|"/"|"%") pow}
	 * 
	 * @return
	 * @throws ParseError
	 */
	private Expr parseMulDiv() throws ParseError {
		Expr expr = parsePow();
		while (m_scanner.isTokenType(TokenType.OPERATOR)) {
			if (matchOperator("*")) {
				expr = AppExpr.create(PrimFuncValue.MUL2, expr, parsePow());
			} else if (matchOperator("/")) {
				expr = AppExpr.create(PrimFuncValue.DIV2, expr, parsePow());
			} else if (matchOperator("%")) {
				expr = AppExpr.create(PrimFuncValue.MOD2, expr, parsePow());
			} else {
				break;
			}
		}
		return expr;
	}

	/**
	 * Parse ["+"|"-"] mulDiv {("+"|"-") mulDiv}
	 * 
	 * @return
	 * @throws ParseError
	 */
	private Expr parseAddSub() throws ParseError {
		Expr expr;
		if (m_scanner.isTokenType(TokenType.OPERATOR)) {
			if (matchOperator("+")) {
				expr = AppExpr.create(PrimFuncValue.PLUS1, parseMulDiv());
			} else if (matchOperator("-")) {
				expr = AppExpr.create(PrimFuncValue.NEG1, parseMulDiv());
			} else {
				expr = parseMulDiv();
			}
		} else {
			expr = parseMulDiv();
		}
		while (m_scanner.isTokenType(TokenType.OPERATOR)) {
			if (matchOperator("+")) {
				expr = AppExpr.create(PrimFuncValue.ADD2, expr, parseMulDiv());
			} else if (matchOperator("-")) {
				expr = AppExpr.create(PrimFuncValue.SUB2, expr, parseMulDiv());
			} else {
				break;
			}
		}
		return expr;
	}

	/**
	 * Parse addSub [("=="|"!="|"<"|"<="|">"|">=") addSub]
	 * 
	 * @return
	 * @throws ParseError
	 */
	private Expr parseConds() throws ParseError {
		Expr expr = parseAddSub();
		if (matchOperator("==")) {
			expr = AppExpr.create(PrimFuncValue.EQ2, expr, parseAddSub());
		} else if (matchOperator("!=")) {
			expr = AppExpr.create(PrimFuncValue.NEQ2, expr, parseAddSub());
		} else if (matchOperator("<")) {
			expr = AppExpr.create(PrimFuncValue.LT2, expr, parseAddSub());
		} else if (matchOperator("<=")) {
			expr = AppExpr.create(PrimFuncValue.LTE2, expr, parseAddSub());
		} else if (matchOperator(">")) {
			expr = AppExpr.create(PrimFuncValue.GT2, expr, parseAddSub());
		} else if (matchOperator(">=")) {
			expr = AppExpr.create(PrimFuncValue.GTE2, expr, parseAddSub());
		}
		return expr;
	}

	/**
	 * Parse conds {"&&" conds}
	 * 
	 * @return
	 */
	private Expr parseAndOp() throws ParseError {
		Expr expr = parseConds();
		while (matchOperator("&&")) {
			expr = new AndExpr(expr, parseConds());
		}
		return expr;
	}

	/**
	 * Parse andOp {"||" andOp}
	 * 
	 * @return
	 */
	private Expr parseOrOp() throws ParseError {
		Expr expr = parseAndOp();
		while (matchOperator("||")) {
			expr = new OrExpr(expr, parseAndOp());
		}
		return expr;
	}

	/**
	 * Parse orOp [(":="|"<-") orOp]. The instanceof is something of a hack to
	 * make the parser overall simpler.
	 */
	private Expr parseStore() throws ParseError {
		Expr expr = parseOrOp();
		if (matchOperator(":=")) {
			if (expr instanceof VariableExpr) {
				expr = new StoreExpr(((VariableExpr) expr).getIdentifier(), parseOrOp());
			} else {
				throw parseError("Can only store into variable");
			}
		} else if (matchOperator("<-")) {
			if (expr instanceof VariableExpr) {
				expr = new UpdateExpr(((VariableExpr) expr).getIdentifier(), parseOrOp());
			} else {
				throw parseError("Can only update variable");
			}
		}
		return expr;
	}

	/**
	 * An expr is just a store
	 * 
	 * @return
	 * @throws ParseError
	 */
	private Expr parseExpr() throws ParseError {
		return parseStore();
	}

	public Expr parseTopExpr() throws ParseError {
		nextToken(); // initialize scanner

		Expr e;
		while (matchOperator(";")) {
			// consume leading semicolons
		}
		e = parseExpr();
		while (matchOperator(";")) {
			while (matchOperator(";")) {
				// consume semicolons
			}
			if (m_scanner.isTokenType(TokenType.EOF)) {
				break;
			}
			e = new SeqExpr(e, parseExpr());
		}
		if (!m_scanner.isTokenType(TokenType.EOF)) {
			throw parseError("Expecting end of input");
		}
		return e;
	}

	/**
	 * Checks whether the current token is the given token, and consumes it.
	 * 
	 * @param type
	 *            the required type (only for tokens which have a token string)
	 * @param token
	 *            the token string
	 * @throws ParseError
	 *             if it doesn't match
	 */
	private void checkToken(TokenType type, String token) throws ParseError {
		if (!(m_scanner.isTokenType(type) && m_scanner.isToken(token))) {
			throw parseError("Expecting " + token);
		}
		nextToken();
	}

	private boolean matchOperator(String token) throws ParseError {
		if (m_scanner.isTokenType(TokenType.OPERATOR) && m_scanner.isToken(token)) {
			nextToken();
			return true;
		} else {
			return false;
		}
	}

	private boolean matchReserved(String token) throws ParseError {
		if (m_scanner.isTokenType(TokenType.RESERVED) && m_scanner.isToken(token)) {
			nextToken();
			return true;
		} else {
			return false;
		}
	}

	private void nextToken() throws ParseError {
		m_scanner.nextToken();
		if (m_scanner.isTokenType(TokenType.ERROR)) {
			throw new ParseError(m_scanner.getError() + "\n" + m_scanner.showLocation());
		}
	}

	private ParseError parseError(String msg) throws ParseError {
		return new ParseError(m_scanner.getError(msg) + "\n" + m_scanner.showLocation());
	}

	public static class ParseError extends Exception {
		public ParseError(String err) {
			super(err);
		}
	}
}
