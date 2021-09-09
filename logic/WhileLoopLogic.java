package logic;

import main.*;
import node.*;

public abstract class WhileLoopLogic {
	public static final PackageNode declare = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"WhileLoop.Declare",
				Resources.WHILE,
				Resources.OPEN_PARENTHESIS,
				new OrNode(AssignmentLogic.content, AssignmentLogic.group),
				Resources.CLOSE_PARENTHESIS,
				Resources.DO,
				new OrNode(
						BodyLogic.content,
						BodyLogic.declare_semicolon
				)
		));
	}
}
