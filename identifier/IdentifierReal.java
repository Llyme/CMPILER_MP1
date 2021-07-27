package identifier;
public class IdentifierReal implements IIdentifier {
	private String name;
	private Boolean predeclared;
	private float value;
	
	public IdentifierReal
	(String name,
	Boolean predeclared,
	float initialValue) {
		this.name = name;
		this.predeclared = predeclared;
		value = initialValue;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDataType() {
		return "real";
	}
	
	public Boolean isPredeclared() {
		return predeclared;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(String lexeme) {
		value = Float.parseFloat(lexeme);
	}

	public Boolean isValid(String lexeme) {
		return lexeme.matches("^-?[0-9]*\\.?[0-9]*$");
	}
}
