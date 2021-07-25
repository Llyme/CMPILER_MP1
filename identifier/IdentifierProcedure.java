package identifier;

import java.util.ArrayList;

public class IdentifierProcedure implements IIdentifier {
	private String name;
	private Boolean predeclared;
	private ArrayList<String> lexemes = new ArrayList<String>();
	private ArrayList<String> token_classes = new ArrayList<String>();
	private ArrayList<String> parameterTypes = new ArrayList<String>();
	/**
	 * Allows extensible parameters.
	 * The extended parameters are the same as the last parameter type.
	 */
	private Boolean varargs;
	
	/**
	 * Passing a parameter type with empty string ignores data type.
	 */
	public IdentifierProcedure
	(String name,
	Boolean predeclared,
	Boolean varargs,
	String... parameterTypes) {
		this.name = name;
		this.predeclared = predeclared;
		this.varargs = varargs;
		
		for (String text : parameterTypes)
			this.parameterTypes.add(text);
	}
	
	public String getName() {
		return name;
	}
	
	public String getDataType() {
		return "procedure";
	}
	
	public Boolean isPredeclared() {
		return predeclared;
	}

	/**
	 * Adds a lexeme-class pair.
	 */
	public void push(String lexeme, String token_class) {
		lexemes.add(lexeme);
		token_classes.add(token_class);
	}
	
	public String getLexeme(int i) {
		return lexemes.get(i);
	}

	public String getTokenClass(int i) {
		return token_classes.get(i);
	}
	
	public int parameterCount() {
		return parameterTypes.size();
	}
	
	public String getParameterType(int i) {
		int last = parameterTypes.size() - 1;
		
		if (i > last && varargs)
			return parameterTypes.get(last);
		
		return parameterTypes.get(i);
	}
	
	public Boolean hasVarargs() {
		return varargs;
	}
	
	@Deprecated
	public Object getValue() {
		return null;
	}

	@Deprecated
	public void setValue(String lexeme) {
	}

	/**
	 * Functions are immutable.
	 */
	@Deprecated
	public Boolean isValid(String lexeme) {
		return false;
	}
}
