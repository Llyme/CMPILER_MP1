package logic;

import main.*;
import node.*;

public abstract class ForLoopLogic {
	public static PackageNode declare = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"ForLoop.Declare",
				Resources.FOR,
				new OrNode(Resources.IDENTIFIER, Resources.PREDECLARED),
				Resources.ASSIGNMENT,
				Resources.INTEGER,
				Resources.TO,
				Resources.INTEGER,
				Resources.DO,
				new OrNode(BodyLogic.declare_semicolon, BodyLogic.content)
		));
	}
}
