package identifier;
public class IdentifierInteger extends Identifier {
	private int value;
	
	public IdentifierInteger(String name, boolean predeclared) {
		super(name, predeclared);
	}
	
	public String getDataType() {
		return "integer";
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(String lexeme) {
		super.setValue();
		this.value = Integer.parseInt(lexeme);
	}

	public boolean isValid(String lexeme) {
		return lexeme.matches("^-?[0-9]*$");
	}
}
