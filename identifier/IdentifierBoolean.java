package identifier;
public class IdentifierBoolean implements IIdentifier {
	private String name;
	private Boolean predeclared;
	private Boolean value;
	
	public IdentifierBoolean(String name, Boolean predeclared, Boolean initialValue) {
		this.name = name;
		this.predeclared = predeclared;
		value = initialValue;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDataType() {
		return "boolean";
	}
	
	public Boolean isPredeclared() {
		return predeclared;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = (Boolean) value;
	}

	public Boolean isValid(String lexeme) {
		return lexeme == "false" || lexeme == "true";
	}
}
