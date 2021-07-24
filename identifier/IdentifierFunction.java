package identifier;

public class IdentifierFunction extends IdentifierProcedure implements IIdentifier {
	private String returnType;
	
	public IdentifierFunction
	(String name,
	Boolean predeclared,
	String returnType,
	String... parameterTypes) {
		super(name, predeclared, parameterTypes);
		this.returnType = returnType;
	}
	
	@Override
	public String getDataType() {
		return "procedure";
	}
	
	public String getReturnType() {
		return returnType;
	}
}
