package identifier;
public class IdentifierType implements IIdentifier {
	private String name;
	private Boolean predeclared;
	
	public IdentifierType(String name, Boolean predeclared) {
		this.name = name;
		this.predeclared = predeclared;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDataType() {
		return "type";
	}
	
	public Boolean isPredeclared() {
		return predeclared;
	}

	public Object getValue() {
		return this;
	}

	public void setValue(Object value) {
	}

	/**
	 * Data types are immutable and should not be edited.
	 */
	public Boolean isValid(String lexeme) {
		return false;
	}
}
