package logic;

import main.*;
import node.*;

public abstract class ArithmeticLogic {
	public static final PackageNode declare = new PackageNode();
	public static final PackageNode content = new PackageNode();
	public static final PackageNode group = new PackageNode();
	
	public static final PackageNode declare_integer = new PackageNode();
	public static final PackageNode content_integer = new PackageNode();
	public static final PackageNode group_integer = new PackageNode();
	
	public static final PackageNode declare_real = new PackageNode();
	public static final PackageNode content_real = new PackageNode();
	public static final PackageNode group_real = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"Arithmetic.Declare",
				new OrNode(
						declare_integer,
						declare_real,
						INode.stack(
								Resources.IDENTIFIER,
								new OrNode(content, null)
						),
						group
				)
		));
		
		content.set(() -> INode.stack(
				"Arithmetic.Content",
				Resources.ARITHMETIC_OPERATOR,
				new OrNode(
						INode.stack(
								Resources.INTEGER,
								new OrNode(content_integer, null)
						),
						INode.stack(
								Resources.REAL,
								new OrNode(content_real, null)
						),
						INode.stack(
								Resources.IDENTIFIER,
								new OrNode(content, null)
						),
						group
				)
		));
		
		group.set(() -> INode.stack(
				"Arithmetic.Group",
				Resources.OPEN_PARENTHESIS,
				new OrNode(group, declare),
				Resources.CLOSE_PARENTHESIS
		));
		
		
		declare_integer.set(() -> INode.stack(
				"Arithmetic.Declare.Integer",
				Resources.INTEGER,
				new OrNode(content_integer, null)
		));
		
		content_integer.set(() -> INode.stack(
				"Arithmetic.Content.Integer",
				Resources.ARITHMETIC_OPERATOR,
				new OrNode(Resources.INTEGER, Resources.IDENTIFIER, group_integer),
				new OrNode(content_integer, null)
		));
		
		group_integer.set(() -> INode.stack(
				"Arithmetic.Group.Integer",
				Resources.OPEN_PARENTHESIS,
				new OrNode(group_integer, declare_integer),
				Resources.CLOSE_PARENTHESIS
		));
		
		
		declare_real.set(() -> INode.stack(
				"Arithmetic.Declare.Real",
				Resources.REAL,
				new OrNode(content_real, null)
		));
		
		content_real.set(() -> INode.stack(
				"Arithmetic.Content.Real",
				Resources.ARITHMETIC_OPERATOR,
				new OrNode(Resources.REAL, Resources.IDENTIFIER, group_real),
				new OrNode(content_real, null)
		));
		
		group_real.set(() -> INode.stack(
				"Arithmetic.Group.Real",
				Resources.OPEN_PARENTHESIS,
				new OrNode(group_real, declare_real),
				Resources.CLOSE_PARENTHESIS
		));
	}
}
