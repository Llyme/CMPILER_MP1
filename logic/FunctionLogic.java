package logic;

import main.Resources;
import node.*;

public abstract class FunctionLogic {
	public static final PackageNode declare = new PackageNode();
	public static final PackageNode parameter = new PackageNode();
	public static final PackageNode parameter_append = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"Function.Declare",
				Resources.FUNCTION,
				Resources.IDENTIFIER,
				Resources.OPEN_PARENTHESIS,
				new OrNode(parameter, null),
				Resources.CLOSE_PARENTHESIS,
				Resources.COLON,
				Resources.DATA_TYPE,
				Resources.SEMICOLON,
				BodyLogic.declare,
				Resources.SEMICOLON,
				new OrNode(declare, ProcedureLogic.declare, null)
		));
		
		parameter.set(() -> INode.stack(
				"Function.Parameter",
				Resources.CONST,
				Resources.IDENTIFIER,
				Resources.COLON,
				Resources.DATA_TYPE,
				new OrNode(parameter_append, null)
		));
		
		parameter_append.set(() -> INode.stack(
				"Function.Parameter.Append",
				Resources.COMMA,
				parameter
		));
	}
}
