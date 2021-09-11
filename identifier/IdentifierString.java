package identifier;
public class IdentifierString extends Identifier {
	private String value;
	
	public IdentifierString(String name, boolean predeclared) {
		super(name, predeclared);
	}
	
	public String getDataType() {
		return "string";
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

	public boolean isValid(String lexeme) {
		if (lexeme.equals("null"))
			return true;
		
		if (lexeme.startsWith("'") && lexeme.endsWith("'"))
			return true;
		
		return false;
	}
}
