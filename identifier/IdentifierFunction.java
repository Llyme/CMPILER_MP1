package identifier;

public class IdentifierFunction extends IdentifierProcedure implements IIdentifier {
	private String returnType;
	
	public IdentifierFunction
	(String name,
	Boolean predeclared,
	Boolean varargs,
	String returnType,
	String... parameterTypes) {
		super(name, predeclared, varargs, parameterTypes);
		this.returnType = returnType;
	}
	
	@Override
	public String getDataType() {
		return "function";
	}
	
	public String getReturnType() {
		return returnType;
	}
}
