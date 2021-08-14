package logic;

import main.*;
import node.*;

public abstract class IfThenElseLogic {
	public static final PackageNode declare = new PackageNode();
	public static final PackageNode body = new PackageNode();
	public static final PackageNode body_single = new PackageNode();
	public static final PackageNode $else = new PackageNode();
	public static final PackageNode $else_body = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"IfThenElse.Declare",
				Resources.IF,
				Resources.OPEN_PARENTHESIS,
				BodyLogic.expression,
				Resources.CLOSE_PARENTHESIS,
				Resources.THEN,
				new OrNode(body, BodyLogic.content)
		));
		
		body.set(() -> INode.stack(
				"IfThenElse.Body",
				BodyLogic.declare,
				new OrNode($else, Resources.SEMICOLON)
		));
		
		body_single.set(() -> INode.stack(
				"IfThenElse.Body.Single",
				BodyLogic.content,
				new OrNode($else, null)
		));
		
		$else.set(() -> INode.stack(
				"IfThenElse.Else",
				Resources.ELSE,
				new OrNode($else_body, BodyLogic.content)
		));
		
		$else_body.set(() -> INode.stack(
				"IfThenElse.Else.Body",
				BodyLogic.declare,
				Resources.SEMICOLON
		));
	}
}
