package scicalc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The scanner is responsible for reading individual tokens, like numbers and
 * operators and names and strings and such.
 * 
 * @author kmill
 *
 */
public class SciScanner {
	private String m_input;
	private int m_index, m_line, m_col;
	private int m_last_line, m_last_col;

	private TokenType m_token_type;
	private String m_token;
	private String m_error;
	private double m_floatval;
	private int m_intval;

	private static Pattern whitespace = Pattern.compile("\\s+"),
			identifier = Pattern.compile("[a-zA-Z_$][a-zA-Z\\d_$]*"),
			number = Pattern.compile("[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?"),
			operator = Pattern.compile("->|[-+*/^%()\\[\\],;]|:=|<-|==|<=|<|>=|>|!=|&&|\\|\\|");

	private static final String[] RESERVED_IDENTS = { "null", "true", "false", "begin", "if", "then", "else", "elif",
			"while", "fun" };

	public SciScanner(String input) {
		m_input = input;
		m_index = 0; // current input location
		m_last_line = m_line = 1;
		m_last_col = m_col = 0;
	}

	/**
	 * Find the next token and update the members
	 */
	public void nextToken() {
		boolean keepGoing = true; // this is to handle consuming comments
		m_token_type = TokenType.ERROR;
		m_error = "Error in SciScanner"; // this shouldn't happen
		Matcher m;
		while (keepGoing) {
			keepGoing = false;
			match(whitespace); // just consume whitespace
			m_last_line = m_line;
			m_last_col = m_col;
			if (isEof()) {
				m_token_type = TokenType.EOF;
			} else if (match("//", true)) {
				while (!isEof() && peek() != '\n') {
					next();
				}
				keepGoing = true;
			} else if (match("/*", true)) {
				// consume characters while not end-of-comment
				while (!isEof() && !match("*/", false)) {
					next();
				}
				if (match("*/", true)) {
					keepGoing = true;
				} else {
					m_token_type = TokenType.ERROR;
					m_error = "Unmatched multiline comment";
				}
			} else if (null != (m = match(operator))) {
				m_token = m.group(0);
				m_token_type = TokenType.OPERATOR;
			} else if (null != (m = match(identifier))) {
				m_token = m.group(0);
				m_token_type = TokenType.IDENTIFIER;
				// check if it is a reserved word:
				for (int i = 0; i < RESERVED_IDENTS.length; i++) {
					if (m_token.equals(RESERVED_IDENTS[i])) {
						m_token_type = TokenType.RESERVED;
						break;
					}
				}
			} else if (null != (m = match(number))) {
				try {
					m_token_type = TokenType.FLOAT;
					m_floatval = Double.parseDouble(m.group(0));
				} catch (NumberFormatException x) {
					m_token_type = TokenType.ERROR;
					m_error = "Invalid number";
				}
			} else {
				m_token_type = TokenType.ERROR;
				m_error = "Unexpected";
			}
		}
	}

	public String getError(String msg) {
		return m_last_line + ":" + m_last_col + ": " + msg;
	}

	public String getError() {
		return getError(m_error);
	}

	public String showLocation() {
		String[] lines = m_input.split("\n");
		int i = m_last_line - 1;
		if (i >= lines.length) {
			return "at end of input";
		} else {
			StringBuffer carat = new StringBuffer();
			for (int j = 0; j < m_last_col; j++) {
				carat.append(' ');
			}
			carat.append('^');
			return lines[i] + "\n" + carat.toString();
		}
	}

	/**
	 * @return whether at end of file
	 */
	private boolean isEof() {
		return m_index >= m_input.length();
	}

	/**
	 * Like next, but does not consume
	 * 
	 * @return the next character, or -1 if at end of file
	 */
	private int peek() {
		if (m_index < m_input.length()) {
			return m_input.charAt(m_index);
		} else {
			return -1;
		}
	}

	/**
	 * Consumes
	 * 
	 * @return the next character, or -1 if at end of file
	 */
	private int next() {
		if (m_index < m_input.length()) {
			char c = m_input.charAt(m_index);
			updateLocation(c);
			return c;
		} else {
			return -1;
		}
	}

	private void updateLocation(char c) {
		switch (c) {
		case '\n':
			m_line++;
			m_col = 0;
			break;
		case '\t':
			m_col += 8;
			break;
		default:
			m_col++;
		}
		m_index++;
	}

	private void updateLocation(String s) {
		for (int i = 0; i < s.length(); i++) {
			updateLocation(s.charAt(i));
		}
	}

	private boolean match(String s, boolean consume) {
		for (int j = 0; j < s.length(); j++) {
			if (m_index + j >= m_input.length()) {
				return false;
			}
			if (s.charAt(j) != m_input.charAt(m_index + j)) {
				return false;
			}
		}
		if (consume) {
			updateLocation(s);
		}
		return true;
	}

	private Matcher match(Pattern p, boolean consume) {
		Matcher m = p.matcher(m_input);
		m.useAnchoringBounds(true);
		m.region(m_index, m_input.length());
		if (m.lookingAt()) {
			if (consume) {
				updateLocation(m.group(0));
			}
			return m;
		} else {
			return null;
		}
	}

	private Matcher match(Pattern p) {
		return match(p, true);
	}

	public TokenType getTokenType() {
		return m_token_type;
	}

	public boolean isTokenType(TokenType type) {
		return m_token_type.equals(type);
	}

	public String getToken() {
		return m_token;
	}

	public boolean isToken(String tok) {
		return m_token.equals(tok);
	}

	public double getFloatVal() {
		return m_floatval;
	}

	public int getIntVal() {
		return m_intval;
	}
}
