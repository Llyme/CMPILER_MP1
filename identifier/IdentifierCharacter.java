package identifier;
public class IdentifierCharacter implements IIdentifier {
	private String name;
	private Boolean predeclared;
	private char value;
	
	public IdentifierCharacter(String name, Boolean predeclared, char initialValue) {
		this.name = name;
		this.predeclared = predeclared;
		value = initialValue;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDataType() {
		return "character";
	}
	
	public Boolean isPredeclared() {
		return predeclared;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = (char) value;
	}

	public Boolean isValid(String lexeme) {
		return lexeme.length() == 1;
	}
}
