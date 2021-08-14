package logic;

import main.*;
import node.*;

public abstract class RootLogic {
	public static final PackageNode declare = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"Root.Declare",
				Resources.PROGRAM,
				Resources.IDENTIFIER,
				Resources.SEMICOLON,
				new OrNode(VarLogic.declare, null),
				new OrNode(ProcedureLogic.declare, FunctionLogic.declare, null),
				BodyLogic.declare,
				Resources.DOT
		));
		
		Resources.error_index.put(
				new String[] { "Program" },
				0
		);
	}
}
