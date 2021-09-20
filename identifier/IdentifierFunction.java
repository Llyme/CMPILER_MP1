package identifier;

import java.util.ArrayList;

import main.Func;

public class IdentifierFunction extends IdentifierProcedure {
	private Identifier returnValue;
	
	public IdentifierFunction
	(String name,
	Boolean predeclared,
	Func<Identifier> returnValueInitializer,
	ArrayList<String> identifiers,
	ArrayList<String> dataTypes) {
		super(name, predeclared, identifiers, dataTypes);
		returnValue = returnValueInitializer.invoke();
	}
	
	public String getDataType() {
		return "function";
	}
	
	public Identifier returnValue() {
		return returnValue;
	}
}
