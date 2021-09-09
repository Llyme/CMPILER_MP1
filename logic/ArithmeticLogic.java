package logic;

import main.*;
import node.*;

public abstract class ArithmeticLogic {
	public static final PackageNode declare = new PackageNode();
	public static final PackageNode content = new PackageNode();
	public static final PackageNode group = new PackageNode();
	
	public static final PackageNode declare_number = new PackageNode();
	public static final PackageNode content_number = new PackageNode();
	public static final PackageNode group_number = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"Arithmetic.Declare",
				new OrNode(
						declare_number,
						INode.stack(
								GenericLogic.IDENTIFIER_OR_CALLABLE,
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
								GenericLogic.NUMBER,
								new OrNode(content_number, null)
						),
						INode.stack(
								GenericLogic.IDENTIFIER_OR_CALLABLE,
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
		
		
		declare_number.set(() -> INode.stack(
				"Arithmetic.Declare.Number",
				GenericLogic.NUMBER,
				new OrNode(content_number, null)
		));
		
		content_number.set(() -> INode.stack(
				"Arithmetic.Content.Number",
				Resources.ARITHMETIC_OPERATOR,
				new OrNode(
						GenericLogic.NUMBER,
						GenericLogic.IDENTIFIER_OR_CALLABLE,
						group_number
				),
				new OrNode(content_number, null)
		));
		
		group_number.set(() -> INode.stack(
				"Arithmetic.Group.Number",
				Resources.OPEN_PARENTHESIS,
				new OrNode(group_number, declare_number),
				Resources.CLOSE_PARENTHESIS
		));
	}
}
