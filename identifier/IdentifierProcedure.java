package identifier;

import java.util.ArrayList;
import java.util.Stack;

import main.Action;

public class IdentifierProcedure extends Identifier {
	private ArrayList<Action> actions = new ArrayList<Action>();
	private String[] identifiers;
	private String[] dataTypes;
	
	/**
	 * Passing a parameter type with empty string ignores data type.
	 * @param name
	 * @param predeclared
	 * @param parameterTypes data types.
	 */
	public IdentifierProcedure
	(String name,
	boolean predeclared,
	ArrayList<String> identifiers,
	ArrayList<String> dataTypes) {
		super(name, predeclared);
		this.identifiers = new String[identifiers.size()];
		this.dataTypes = new String[dataTypes.size()];
		identifiers.toArray(this.identifiers);
		dataTypes.toArray(this.dataTypes);
	}
	
	public IdentifierProcedure(String name, boolean predeclared) {
		super(name, predeclared);
		identifiers = new String[0];
		dataTypes = new String[0];
	}
	
	public String getDataType() {
		return "procedure";
	}
	
	public void append(Action action) {
		actions.add(action);
	}
	
	public Stack<Action> stack() {
		Stack<Action> stack = new Stack<Action>();
		
		for (int i = actions.size() - 1; i >= 0; i--)
			stack.push(actions.get(i));
		
		return stack;
	}
	
	/**
	 * Returns how many parameters this procedure has.
	 * @return
	 */
	public int parameterLength() {
		return identifiers.length;
	}
	
	/**
	 * Returns the name of the parameter at the given index,
	 * starting at index 0.
	 */
	public String getIdentifier(int i) {
		return identifiers[i];
	}
	
	/**
	 * Returns the data type of the parameter at the given index,
	 * starting at index 0.
	 */
	public String getDataType(int i) {
		return dataTypes[i];
	}
	
	/**
	 * @deprecated This is a callable. It doesn't contain any values.
	 */
	@Deprecated
	public Object getValue() {
		return null;
	}

	/**
	 * @deprecated This is a callable. It doesn't contain any values.
	 */
	@Deprecated
	public void setValue(String lexeme) {
	}

	/**
	 * @deprecated Callables are immutable.
	 */
	@Deprecated
	public boolean isValid(String lexeme) {
		return false;
	}
}
