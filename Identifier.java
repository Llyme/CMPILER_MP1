
public class Identifier {
	private String name;
	private DataType type;
	private Boolean predeclared;
	
	public Identifier(String name, DataType type, Boolean predeclared) {
		this.name = name.toLowerCase();
		this.type = type;
		this.predeclared = predeclared;
	}
	
	public String getName() {
		return name;
	}
	
	public DataType getType() {
		return type;
	}
	
	public Boolean isPredeclared() {
		return predeclared;
	}
}
