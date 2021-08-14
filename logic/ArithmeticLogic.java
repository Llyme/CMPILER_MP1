package logic;

import main.*;
import node.*;

public abstract class ArithmeticLogic {
	public static final PackageNode declare = new PackageNode();
	public static final PackageNode content = new PackageNode();
	public static final PackageNode group = new PackageNode();
	public static final OrNode token = new OrNode(
			Resources.INTEGER,
			Resources.REAL,
			new OrNode(Resources.IDENTIFIER, Resources.PREDECLARED)
	);
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"Arithmetic.Declare",
				new OrNode(token, group),
				new OrNode(content, null),
				Resources.SEMICOLON
		));
		
		content.set(() -> INode.stack(
				"Arithmetic.Content",
				Resources.ARITHMETIC_OPERATOR,
				new OrNode(token, group),
				new OrNode(content, null)
		));
		
		group.set(() -> INode.stack(
				"Arithmetic.Group",
				Resources.OPEN_PARENTHESIS,
				new OrNode(group, declare),
				Resources.CLOSE_PARENTHESIS
		));
	}
}
