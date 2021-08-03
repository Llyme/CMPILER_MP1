package identifier;
public class IdentifierArray implements IIdentifier {
	private String name;
	private Boolean predeclared;
	private IIdentifier[] values;
	
	public IdentifierArray(String name, Boolean predeclared, String genericType, int size) {
		this.name = name;
		this.predeclared = predeclared;
		values = new IIdentifier[size];
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

	@Deprecated
	public Object getValue() {
		return null;
	}

	@Deprecated
	public void setValue(String lexeme) {
	}

	public Boolean isValid(String lexeme) {
		return lexeme.matches("^-?[0-9]*$");
	}
}
