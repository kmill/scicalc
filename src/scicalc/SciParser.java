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
            Expr alt = parseExprSeq();
            return new IfExpr(cond, cons, alt);
        } else if (matchReserved("elif")) {
            Expr cond2 = parseExprSeq();
            checkToken(TokenType.RESERVED, "do");
            Expr cons2 = parseExprSeq();
            return new IfExpr(cond, cons, parseIf(cond2, cons2));
        } else {
            return new IfExpr(cond, cons, NullValue.INSTANCE);
        }
    }

    private Expr parseTerm() throws ParseError {
        Expr e;
        if (m_scanner.isTokenType(TokenType.FLOAT)) {
            e = new FloatValue(m_scanner.getFloatVal());
            nextToken();
        } else if (m_scanner.isTokenType(TokenType.STRING)) {
            e = new StringValue(m_scanner.getToken());
            nextToken();
        } else if (matchOperator("!")) {
            e = AppExpr.create(PrimFuncValue.NOT1, parseTerm());
        } else if (matchOperator("(")) {
            e = parseExprSeq();
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
            // "if" expr "do" expr {"elif" expr "do"} ["else" expr] "end"
            Expr cond = parseExprSeq();
            checkToken(TokenType.RESERVED, "do");
            Expr cons = parseExprSeq();
            e = parseIf(cond, cons);
            checkToken(TokenType.RESERVED, "end");
        } else if (matchReserved("while")) {
            // "while" expr ["do" expr] "end"
            Expr cond = parseExprSeq();
            Expr body;
            if (matchReserved("do")) {
                body = parseExprSeq();
            } else {
                body = NullValue.INSTANCE;
            }
            e = new WhileExpr(cond, body);
            checkToken(TokenType.RESERVED, "end");
        } else if (matchReserved("fun")) {
            // "fun" [name] "("param1, param2, ...")" body "end"
            // if name is present, then this is shorthand for the expression
            // name := "fun" "("param1, param2, ...")" body "end"
            String name = null;
            if (m_scanner.isTokenType(TokenType.IDENTIFIER)) {
                name = m_scanner.getToken();
                nextToken();
            }
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
            Expr body = parseExprSeq();
            e = new FunExpr(parameters.toArray(new String[parameters.size()]), body);
            checkToken(TokenType.RESERVED, "end");
            if (name != null) {
                e = new StoreExpr(name, e);
            }
        } else if (matchReserved("block")) {
            // "block" [label] "do" body "end"
            String label = null;
            if (!matchReserved("do")) {
                if (!m_scanner.isTokenType(TokenType.IDENTIFIER)) {
                    throw parseError("Expecting identifier");
                }
                label = m_scanner.getToken();
                nextToken();
                checkToken(TokenType.RESERVED, "do");
            }
            Expr body = parseExprSeq();
            e = new BlockExpr(label, body);
            checkToken(TokenType.RESERVED, "end");
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
                // [index1,index2,...]
                e = AppExpr.create(PrimFuncValue.GET, e, parseExpr());
                while (matchOperator(",")) {
                    e = AppExpr.create(PrimFuncValue.GET, e, parseExpr());
                }
                checkToken(TokenType.OPERATOR, "]");
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
        if (matchOperator("^")) {
            expr = AppExpr.create(PrimFuncValue.POW2, expr, parsePow());
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
        while (true) {
            if (matchOperator("*")) {
                expr = AppExpr.create(PrimFuncValue.MUL2, expr, parsePow());
            } else if (matchOperator("/")) {
                expr = AppExpr.create(PrimFuncValue.DIV2, expr, parsePow());
            } else if (matchReserved("div")) {
                expr = AppExpr.create(PrimFuncValue.IDIV2, expr, parsePow());
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
        while (true) {
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
            } else if (expr instanceof AppExpr && ((AppExpr) expr).getFunc() == PrimFuncValue.GET) {
                AppExpr aexpr = (AppExpr) expr;
                Expr[] args = aexpr.getArgs();
                expr = new AppExpr(PrimFuncValue.SET, new Expr[] { args[0], args[1], parseOrOp() });
            } else {
                throw parseError("Can only update variable or index");
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

    private Expr parseExprSeq() throws ParseError {
        // parse {";"} expr {";"|expr} [";"]
        while (matchOperator(";")) {
            // consume leading semicolons
        }
        Expr e = parseExpr();
        while (matchOperator(";")) {
            while (matchOperator(";")) {
                // consume semicolons
            }
            if (m_scanner.isTokenType(TokenType.EOF)
                    || m_scanner.isTokenType(TokenType.OPERATOR) && (m_scanner.isToken(")"))
                    || m_scanner.isTokenType(TokenType.RESERVED) && (m_scanner.isToken("elif")
                            || m_scanner.isToken("else") || m_scanner.isToken("end") || m_scanner.isToken("do"))) {
                break;
            }
            e = new SeqExpr(e, parseExpr());
        }
        return e;
    }

    public Expr parseTopExpr() throws ParseError {
        nextToken(); // initialize scanner

        Expr e = parseExprSeq();
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
