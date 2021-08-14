package logic;

import main.Resources;
import node.INode;
import node.OrNode;
import node.PackageNode;

public abstract class ProcedureLogic {
	public static final PackageNode declare = new PackageNode();
	public static final PackageNode parameter = new PackageNode();
	public static final PackageNode parameter_append = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"Procedure.Declare",
				Resources.PROCEDURE,
				Resources.IDENTIFIER,
				Resources.OPEN_PARENTHESIS,
				new OrNode(parameter, null),
				Resources.CLOSE_PARENTHESIS,
				Resources.SEMICOLON,
				BodyLogic.declare,
				Resources.SEMICOLON,
				new OrNode(declare, FunctionLogic.declare, null)
		));
		
		parameter.set(() -> INode.stack(
				"Procedure.Parameter",
				Resources.CONST,
				Resources.IDENTIFIER,
				Resources.COLON,
				Resources.DATA_TYPE,
				new OrNode(parameter_append, null)
		));
		
		parameter_append.set(() -> INode.stack(
				"Procedure.Parameter.Append",
				Resources.COMMA,
				parameter
		));
	}
}
