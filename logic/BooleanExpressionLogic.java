package logic;

import main.Resources;
import node.INode;
import node.OrNode;
import node.PackageNode;

public abstract class BooleanExpressionLogic {
	public static final PackageNode declare = new PackageNode();
	public static final PackageNode declare_number = new PackageNode();
	public static final PackageNode declare_string = new PackageNode();
	public static final PackageNode declare_boolean = new PackageNode();
	public static final PackageNode declare_identifier = new PackageNode();
	
	public static final PackageNode negation = new PackageNode();
	
	public static final PackageNode conditional_operator = new PackageNode();
	
	public static final PackageNode group = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"Body.Expression",
				new OrNode(
						group,
						negation,
						INode.stack(ArithmeticLogic.declare_number, declare_number),
						INode.stack(Resources.STRING, declare_string),
						INode.stack(Resources.BOOLEAN, declare_boolean),
						INode.stack(GenericLogic.IDENTIFIER_OR_CALLABLE, declare_identifier)
				)
		));
		
		declare_number.set(() -> INode.stack(
				"BooleanExpression.Declare.Number",
				Resources.RELATIONAL_OPERATOR,
				new OrNode(
						GenericLogic.NUMBER,
						GenericLogic.IDENTIFIER_OR_CALLABLE
				),
				new OrNode(
						ArithmeticLogic.content_number,
						null
				),
				new OrNode(conditional_operator, null)
		));
		
		declare_string.set(() -> INode.stack(
				"BooleanExpression.Declare.String",
				Resources.RELATIONAL_OPERATOR,
				new OrNode(
						Resources.STRING,
						GenericLogic.IDENTIFIER_OR_CALLABLE
				),
				new OrNode(conditional_operator, null)
		));
		
		declare_boolean.set(() -> INode.stack(
				"BooleanExpression.Declare.Boolean",
				new OrNode(
						INode.stack(
								Resources.RELATIONAL_OPERATOR,
								new OrNode(
										negation,
										Resources.BOOLEAN,
										GenericLogic.IDENTIFIER_OR_CALLABLE
								)
						),
						null
				),
				new OrNode(conditional_operator, null)
		));
		
		declare_identifier.set(() -> INode.stack(
				"BooleanExpression.Declare_Identifier",
				Resources.RELATIONAL_OPERATOR,
				new OrNode(
						Resources.INTEGER,
						Resources.REAL,
						Resources.STRING,
						Resources.BOOLEAN,
						GenericLogic.IDENTIFIER_OR_CALLABLE,
						negation,
						group
				),
				new OrNode(conditional_operator, null)
		));
		
		negation.set(() -> INode.stack(
				"Body.Expression.Negate",
				Resources.NOT,
				new OrNode(group, declare_boolean, declare_identifier)
		));
		
		conditional_operator.set(() -> INode.stack(
				"Body.Expression.Boolean",
				Resources.BOOLEAN_CONDITIONAL_OPERATOR,
				new OrNode(declare, group, negation)
		));
		
		group.set(() -> INode.stack(
				"Body.Group",
				Resources.OPEN_PARENTHESIS,
				new OrNode(declare, group, negation),
				Resources.CLOSE_PARENTHESIS,
				new OrNode(conditional_operator, null)
		));
	}
}
