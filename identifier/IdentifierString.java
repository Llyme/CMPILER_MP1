package identifier;
public class IdentifierString implements IIdentifier {
	private String name;
	private Boolean predeclared;
	private String value;
	
	public IdentifierString(String name, Boolean predeclared, String initialValue) {
		this.name = name;
		this.predeclared = predeclared;
		value = initialValue;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDataType() {
		return "string";
	}
	
	public Boolean isPredeclared() {
		return predeclared;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(String lexeme) {
		if (lexeme == "null")
			this.value = null;
		else
			this.value = lexeme.substring(1, lexeme.length() - 1);
	}

	public Boolean isValid(String lexeme) {
		return lexeme.length() == 1;
	}
}
