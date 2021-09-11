package identifier;
public class IdentifierType extends Identifier {
	private String value;
	
	/**
	 * Name is also the value.
	 */
	public IdentifierType(String name) {
		super(name, true);
		super.setValue();
		this.value = name;
	}
	
	public String getDataType() {
		return "type";
	}
	
	public Object getValue() {
		return value;
	}

	/**
	 * @deprecated This is immutable.
	 */
	@Deprecated
	public void setValue(String lexeme) {
	}

	/**
	 * @deprecated This is immutable.
	 */
	@Deprecated
	public boolean isValid(String lexeme) {
		return false;
	}
}
