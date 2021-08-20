package logic;

import main.Resources;
import node.INode;
import node.OrNode;
import node.PackageNode;

public abstract class BooleanExpressionLogic {
	public static final PackageNode declare = new PackageNode();
	public static final PackageNode declare_integer = new PackageNode();
	public static final PackageNode declare_real = new PackageNode();
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
						INode.stack(ArithmeticLogic.declare_integer, declare_integer),
						INode.stack(ArithmeticLogic.declare_real, declare_real),
						INode.stack(Resources.STRING, declare_string),
						INode.stack(Resources.BOOLEAN, declare_boolean),
						INode.stack(Resources.IDENTIFIER, declare_identifier)
				)
		));
		
		declare_integer.set(() -> INode.stack(
				"BooleanExpression.Declare.Integer",
				Resources.RELATIONAL_OPERATOR,
				new OrNode(
						Resources.INTEGER,
						Resources.IDENTIFIER
				),
				new OrNode(
						ArithmeticLogic.content_integer,
						null
				),
				new OrNode(conditional_operator, null)
		));
		
		declare_real.set(() -> INode.stack(
				"BooleanExpression.Declare.Real",
				Resources.RELATIONAL_OPERATOR,
				new OrNode(
						Resources.REAL,
						Resources.IDENTIFIER
				),
				new OrNode(
						ArithmeticLogic.content_real,
						null
				),
				new OrNode(conditional_operator, null)
		));
		
		declare_string.set(() -> INode.stack(
				"BooleanExpression.Declare.String",
				Resources.RELATIONAL_OPERATOR,
				new OrNode(
						Resources.STRING,
						Resources.IDENTIFIER
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
										Resources.IDENTIFIER
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
						Resources.IDENTIFIER,
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
