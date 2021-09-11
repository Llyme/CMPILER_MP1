package identifier;
public abstract class Identifier {
	private String name;
	private boolean predeclared;
	private boolean initialized;
	
	public Identifier(String name, boolean predeclared) {
		this.name = name;
		this.predeclared = predeclared;
	}
	
	/**
	 * Name of this identifier.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * If this identifier was declared before the scanner started.
	 */
	public boolean isPredeclared() {
		return predeclared;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	protected void setValue() {
		initialized = true;
	}
	
	/**
	 * The value stored in this identifier.
	 */
	public abstract Object getValue();

	/**
	 * Overwrites the value stored in this identifier.
	 */
	public abstract void setValue(String lexeme);
	
	/**
	 * Name of the IdentifierType associated with this identifier.
	 * @return
	 */
	public abstract String getDataType();
	
	/**
	 * If the given lexeme is a valid value for this identifier.
	 */
	public abstract boolean isValid(String lexeme);
}
