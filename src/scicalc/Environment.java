package scicalc;

import java.util.HashMap;

/**
 * An Environment is what holds the current values for variables.
 * 
 * @author kmill
 *
 */
public class Environment {
	private HashMap<String, Value> m_variables;
	private Environment m_parent;

	public Environment() {
		m_variables = new HashMap<>();
	}

	public Environment(Environment parent) {
		this();
		m_parent = parent;
	}

	public Value lookup(String identifier) {
		if (m_variables.containsKey(identifier)) {
			return m_variables.get(identifier);
		} else if (m_parent != null) {
			return m_parent.lookup(identifier);
		} else {
			throw new NoSuchVariableException(identifier);
		}
	}

	public void set(String identifier, Value value) {
		m_variables.put(identifier, value);
	}

	public void update(String identifier, Value value) {
		if (m_variables.containsKey(identifier)) {
			m_variables.put(identifier, value);
		} else if (m_parent != null) {
			m_parent.update(identifier, value);
		} else {
			throw new NoSuchVariableException(identifier);
		}
	}

	public void set(String identifier, double value) {
		set(identifier, new FloatValue(value));
	}

	public static class NoSuchVariableException extends RuntimeException {
		public NoSuchVariableException(String identifier) {
			super("No such variable " + identifier);
		}
	}
}
