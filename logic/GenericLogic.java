package logic;

import main.*;
import node.*;

public abstract class GenericLogic {
	public static final PackageNode IDENTIFIER_OR_CALLABLE = new PackageNode();
	
	public static void initialize() {
		IDENTIFIER_OR_CALLABLE.set(() -> INode.stack(
				"IDENTIFIER_OR_CALLABLE",
				Resources.IDENTIFIER,
				new OrNode(
						INode.stack(
								Resources.OPEN_PARENTHESIS,
								new OrNode(
										AssignmentLogic.content,
										AssignmentLogic.group
								),
								Resources.CLOSE_PARENTHESIS
						),
						null
				)
		));
	}
}
