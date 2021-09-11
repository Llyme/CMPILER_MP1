package identifier;

import java.util.ArrayList;

import main.LexemeTokenPair;

public class IdentifierProcedure extends Identifier {
	private ArrayList<LexemeTokenPair> pairs = new ArrayList<LexemeTokenPair>();
	private String[] parameterTypes;
	
	/**
	 * Passing a parameter type with empty string ignores data type.
	 * @param name
	 * @param predeclared
	 * @param parameterTypes data types.
	 */
	public IdentifierProcedure
	(String name,
	boolean predeclared,
	String... parameterTypes) {
		super(name, predeclared);
		this.parameterTypes = parameterTypes;
	}
	
	public String getDataType() {
		return "procedure";
	}

	/**
	 * Adds a lexeme-token pair.
	 */
	public void append(String lexeme, String token_class) {
		pairs.add(new LexemeTokenPair(lexeme, token_class));
	}
	
	public int pairsLength() {
		return pairs.size();
	}
	
	public LexemeTokenPair get(int index) {
		return pairs.get(index);
	}
	
	public int parameterLength() {
		return parameterTypes.length;
	}
	
	public String getParameterType(int i) {
		return parameterTypes[i];
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
