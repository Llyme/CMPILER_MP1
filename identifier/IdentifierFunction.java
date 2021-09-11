package identifier;

public class IdentifierFunction extends IdentifierProcedure {
	private String returnType;
	
	public IdentifierFunction
	(String name,
	Boolean predeclared,
	Boolean varargs,
	String returnType,
	String... parameterTypes) {
		super(name, predeclared, parameterTypes);
		this.returnType = returnType;
	}
	
	public String getDataType() {
		return "function";
	}
	
	public String getReturnType() {
		return returnType;
	}
}
