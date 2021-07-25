package identifier;
public class IdentifierInteger implements IIdentifier {
	private String name;
	private Boolean predeclared;
	private int value;
	
	public IdentifierInteger(String name, Boolean predeclared, int initialValue) {
		this.name = name;
		this.predeclared = predeclared;
		value = initialValue;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDataType() {
		return "integer";
	}
	
	public Boolean isPredeclared() {
		return predeclared;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(String lexeme) {
		this.value = Integer.parseInt(lexeme);
	}

	public Boolean isValid(String lexeme) {
		return lexeme.matches("^-?[0-9]*$");
	}
}
