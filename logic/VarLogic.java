package logic;

import main.*;
import node.*;

public abstract class VarLogic {
	public static final PackageNode declare = new PackageNode();
	
	public static final PackageNode identifier = new PackageNode();
	public static final PackageNode identifier_append = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"Var.Declare",
				Resources.VAR,
				identifier
		));
		
		identifier.set(() -> INode.stack(
				"Var.Identifier",
				Resources.IDENTIFIER,
				new OrNode(identifier_append, null),
				Resources.COLON,
				Resources.DATA_TYPE,
				Resources.SEMICOLON,
				new OrNode(identifier, null)
		));
		
		identifier_append.set(() -> INode.stack(
				"Var.Identifier.Append",
				Resources.COMMA,
				Resources.IDENTIFIER,
				new OrNode(identifier_append, null)
		));
	}
}
