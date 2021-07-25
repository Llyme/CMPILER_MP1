package identifier;
public interface IIdentifier {
	/**
	 * Name of this identifier.
	 */
	public String getName();
	
	/**
	 * Name of the IdentifierType associated with this identifier.
	 * @return
	 */
	public String getDataType();
	
	/**
	 * If this identifier was declared before the scanner started.
	 */
	public Boolean isPredeclared();
	
	/**
	 * If the given lexeme is a valid value for this identifier.
	 */
	public Boolean isValid(String lexeme);
	
	/**
	 * The value stored in this identifier.
	 */
	public Object getValue();
	
	/**
	 * Overwrites the value stored in this identifier.
	 */
	public void setValue(String lexeme);
}
