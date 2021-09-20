package identifier;

import main.Func;

public class IdentifierArray extends Identifier {
	private String genericType;
	private Identifier[] values;
	/**
	 * Starting index.
	 */
	private int start;
	
	public IdentifierArray
	(String name,
	boolean predeclared,
	String genericType,
	int start,
	int size,
	Func<Identifier> initializer) {
		super(name, predeclared);
		super.setValue(); // Initialize this array.
		values = new Identifier[size];
		this.start = start;
		
		for (int i = 0; i < size; i++)
			values[i] = initializer.invoke();
	}
	
	public String getDataType() {
		return "array." + genericType;
	}
	
	/**
	 * Index should also include the starting index.
	 */
	public Identifier getValue(int index) {
		return values[index - start];
	}

	public boolean indexValid(int index) {
		index = index - start;
		
		return index >= 0 && index < values.length;
	}
	
	public boolean isValid(String lexeme) {
		return lexeme.matches("^-?[0-9]*$");
	}

	/**
	 * @deprecated Use `getValue(int)` instead.
	 */
	@Deprecated
	public Object getValue() {
		return null;
	}

	/**
	 * @deprecated Values are independent on each identifiers.
	 */
	@Deprecated
	public void setValue(String lexeme) {
	}
}
