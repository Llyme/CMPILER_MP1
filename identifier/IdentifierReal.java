package identifier;
public class IdentifierReal extends Identifier {
	private float value;
	
	public IdentifierReal(String name, boolean predeclared) {
		super(name, predeclared);
	}
	
	public String getDataType() {
		return "real";
	}

	public Object getValue() {
		return value;
	}

	public void setValue(String lexeme) {
		value = Float.parseFloat(lexeme);
	}

	public boolean isValid(String lexeme) {
		return lexeme.matches("^-?[0-9]*\\.?[0-9]*$");
	}
}
