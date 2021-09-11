package identifier;
public class IdentifierCharacter extends Identifier {
	private char value;
	
	public IdentifierCharacter(String name, boolean predeclared) {
		super(name, predeclared);
	}
	
	public String getDataType() {
		return "character";
	}

	public Object getValue() {
		return value;
	}

	public void setValue(String lexeme) {
		this.value = lexeme.charAt(0);
	}

	public boolean isValid(String lexeme) {
		return lexeme.length() == 1;
	}
}
