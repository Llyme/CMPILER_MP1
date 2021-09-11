package identifier;
public class IdentifierBoolean extends Identifier {
	private boolean value;
	
	public IdentifierBoolean(String name, Boolean predeclared) {
		super(name, predeclared);
	}
	
	public IdentifierBoolean(String name, Boolean predeclared, Boolean initialValue) {
		this(name, predeclared);
		setValue(initialValue.toString());
	}
	
	public String getDataType() {
		return "boolean";
	}

	public Object getValue() {
		return value;
	}

	public void setValue(String lexeme) {
		super.setValue();
		this.value = Boolean.parseBoolean(lexeme);
	}

	public boolean isValid(String lexeme) {
		return lexeme == "false" || lexeme == "true";
	}
}
