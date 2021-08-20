package logic;

import main.*;
import node.*;

public abstract class AssignmentLogic {
	public static final PackageNode declare = new PackageNode();
	
	public static final PackageNode group = new PackageNode();
	
	public static final PackageNode content = new PackageNode();
	public static final PackageNode content_integer = new PackageNode();
	public static final PackageNode content_real = new PackageNode();
	public static final PackageNode content_boolean = new PackageNode();
	public static final PackageNode content_string = new PackageNode();
	public static final PackageNode content_identifier = new PackageNode();

	public static final PackageNode arithmetic_integer = new PackageNode();
	public static final PackageNode arithmetic_real = new PackageNode();
	
	public static final PackageNode relational_integer = new PackageNode();
	public static final PackageNode relational_real = new PackageNode();
	public static final PackageNode relational_boolean = new PackageNode();
	public static final PackageNode relational_string = new PackageNode();
	public static final PackageNode relational_identifier = new PackageNode();
	
	/**
	 * 0 = None;
	 * 1 = Boolean;
	 * 2 = Arithmetic Integer;
	 * 3 = Arithmetic Real;
	 */
	private static int mode = 0;

	private static final ConditionNode ARITHMETIC_OPERATOR = new ConditionNode(
			"Arithmetic Operator",
			(lexeme, token_class) -> {
				boolean flag =
						mode != 1 &&
						Resources.ARITHMETIC_OPERATOR.parse(lexeme, token_class);
				
				if (flag)
					mode = 1;
				
				return flag;
			}
		);

	private static final ConditionNode RELATIONAL_OPERATOR = new ConditionNode(
			"Relational Operator",
			(lexeme, token_class) -> {
				boolean flag =
						(mode == 0 || mode == 1) &&
						Resources.RELATIONAL_OPERATOR.parse(lexeme, token_class);
				
				if (flag)
					mode = 2;
				
				return flag;
			}
		);

	private static final ConditionNode BOOLEAN_CONDITIONAL_OPERATOR = new ConditionNode(
			"Boolean Conditional Operator",
			(lexeme, token_class) -> {
				boolean flag =
						mode != 1 &&
						Resources.BOOLEAN_CONDITIONAL_OPERATOR.parse(
								lexeme,
								token_class
						);
				
				if (flag)
					mode = 2;
				
				return flag;
			}
	);
	
	private static final ConditionNode ASSIGNMENT = new ConditionNode(
			"Assignment",
			(lexeme, token_class) -> {
				mode = 0;
				return Resources.ASSIGNMENT.parse(
						lexeme,
						token_class
				);
			}
	);
	
	private static final ConditionNode BOOLEAN = new ConditionNode(
			"Boolean",
			(lexeme, token_class) -> {
				boolean flag =
						(mode == 0 || mode == 1) &&
						Resources.BOOLEAN.parse(
								lexeme,
								token_class
						);
				
				if (flag)
					mode = 1;
				
				return flag;
			}
	);
	
	private static final ConditionNode NOT = new ConditionNode(
			"Not",
			(lexeme, token_class) -> {
				boolean flag =
						(mode == 0 || mode == 1) &&
						Resources.NOT.parse(
								lexeme,
								token_class
						);
				
				if (flag)
					mode = 1;
				
				return flag;
			}
	);
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"Assignment.Declare",
				ASSIGNMENT,
				new OrNode(content, group)
		));
		
		group.set(() -> INode.stack(
				"Assignment.Group",
				Resources.OPEN_PARENTHESIS,
				new OrNode(content, group),
				Resources.CLOSE_PARENTHESIS,
				new OrNode(
						arithmetic_integer,
						arithmetic_real,
						relational_integer,
						relational_real,
						relational_boolean,
						relational_string,
						relational_identifier,
						INode.stack(
								BOOLEAN_CONDITIONAL_OPERATOR,
								new OrNode(
										BooleanExpressionLogic.declare,
										BooleanExpressionLogic.group,
										BooleanExpressionLogic.negation
								)
						)
				)
		));
		
		content.set(() -> INode.stack(
				"Assignment.Content",
				new OrNode(
						content_integer,
						content_real,
						content_string,
						content_boolean,
						content_identifier
				)
		));
		
		content_integer.set(() -> INode.stack(
				"Assignment.Content.Integer",
				Resources.INTEGER,
				new OrNode(
						arithmetic_integer,
						relational_integer,
						null
				)
		));
		
		content_real.set(() -> INode.stack(
				"Assignment.Content.Real",
				Resources.REAL,
				new OrNode(
						arithmetic_real,
						relational_real,
						null
				)
		));
		
		content_boolean.set(() -> INode.stack(
				"Assignment.Content.Boolean",
				new OrNode(
						INode.stack(
								NOT,
								new OrNode(
										Resources.BOOLEAN,
										Resources.IDENTIFIER
								)
						),
						BOOLEAN
				),
				new OrNode(
						relational_boolean,
						null
				)
		));
		
		content_string.set(() -> INode.stack(
				"Assignment.Content.String",
				Resources.STRING,
				new OrNode(
						relational_string,
						null
				)
		));
		
		content_identifier.set(() -> INode.stack(
				"Assignment.Content.String",
				Resources.IDENTIFIER,
				new OrNode(
						arithmetic_integer,
						arithmetic_real,
						relational_integer,
						relational_real,
						relational_boolean,
						relational_string,
						relational_identifier,
						null
				)
		));
		
		arithmetic_integer.set(() -> INode.stack(
				"Assignment.Arithmetic.Integer",
				ARITHMETIC_OPERATOR,
				new OrNode(Resources.INTEGER, Resources.IDENTIFIER, ArithmeticLogic.group_integer),
				new OrNode(ArithmeticLogic.content_integer, null)
		));
		
		arithmetic_real.set(() -> INode.stack(
				"Assignment.Arithmetic.Real",
				ARITHMETIC_OPERATOR,
				new OrNode(Resources.REAL, Resources.IDENTIFIER, ArithmeticLogic.group_real),
				new OrNode(ArithmeticLogic.content_real, null)
		));
		
		relational_integer.set(() -> INode.stack(
				"Assignment.Conditional.Integer",
				RELATIONAL_OPERATOR,
				new OrNode(
						Resources.INTEGER,
						Resources.IDENTIFIER
				),
				new OrNode(
						ArithmeticLogic.content_integer,
						null
				),
				new OrNode(
						BooleanExpressionLogic.conditional_operator,
						null
				)
		));
		
		relational_real.set(() -> INode.stack(
				"Assignment.Conditional.Real",
				RELATIONAL_OPERATOR,
				new OrNode(
						Resources.REAL,
						Resources.IDENTIFIER
				),
				new OrNode(
						ArithmeticLogic.content_real,
						null
				),
				new OrNode(
						BooleanExpressionLogic.conditional_operator,
						null
				)
		));
		
		relational_boolean.set(() -> INode.stack(
				"Assignment.Conditional.Integer",
				RELATIONAL_OPERATOR,
				new OrNode(
						BooleanExpressionLogic.negation,
						Resources.BOOLEAN,
						Resources.IDENTIFIER
				),
				new OrNode(
						BooleanExpressionLogic.conditional_operator,
						null
				)
		));
		
		relational_string.set(() -> INode.stack(
				"Assignment.Conditional.String",
				RELATIONAL_OPERATOR,
				new OrNode(
						Resources.STRING,
						Resources.IDENTIFIER
				),
				new OrNode(
						BooleanExpressionLogic.conditional_operator,
						null
				)
		));
		
		relational_identifier.set(() -> INode.stack(
				"Assignment.Conditional.String",
				RELATIONAL_OPERATOR,
				new OrNode(
						Resources.INTEGER,
						Resources.REAL,
						Resources.BOOLEAN,
						Resources.STRING,
						Resources.IDENTIFIER
				),
				new OrNode(
						BooleanExpressionLogic.conditional_operator,
						null
				)
		));
	}
}
