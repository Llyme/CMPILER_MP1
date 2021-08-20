package logic;

import main.*;
import node.*;

public abstract class ForLoopLogic {
	public static PackageNode declare = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"ForLoop.Declare",
				Resources.FOR,
				Resources.IDENTIFIER,
				Resources.ASSIGNMENT,
				new OrNode(Resources.INTEGER, Resources.IDENTIFIER),
				Resources.TO,
				new OrNode(Resources.INTEGER, Resources.IDENTIFIER),
				Resources.DO,
				new OrNode(BodyLogic.declare_semicolon, BodyLogic.content)
		));
	}
}
