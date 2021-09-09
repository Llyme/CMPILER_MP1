package logic;

import main.*;
import node.*;

public abstract class AssignmentLogic {
	public static final PackageNode declare = new PackageNode();
	
	public static final PackageNode group = new PackageNode();
	
	public static final PackageNode content = new PackageNode();
	public static final PackageNode content_number = new PackageNode();
	public static final PackageNode content_boolean = new PackageNode();
	public static final PackageNode content_string = new PackageNode();
	public static final PackageNode content_identifier = new PackageNode();

	public static final PackageNode arithmetic_number = new PackageNode();
	
	public static final PackageNode relational_number = new PackageNode();
	public static final PackageNode relational_boolean = new PackageNode();
	public static final PackageNode relational_string = new PackageNode();
	public static final PackageNode relational_identifier = new PackageNode();
	
	/**
	 * 0 = None;
	 * 1 = Arithmetic;
	 * 2 = Relational & Conditional;
	 */
	private static int mode = 0;

	private static final ConditionNode ARITHMETIC_OPERATOR = new ConditionNode(
			"Arithmetic Operator",
			(lexeme, token_class) -> {
				boolean flag =
						mode != 2 &&
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
						mode != 1 &&
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
	
	private static final ConditionNode BOOLEAN = new ConditionNode(
			"Boolean",
			(lexeme, token_class) -> {
				boolean flag =
						mode != 1 &&
						Resources.BOOLEAN.parse(
								lexeme,
								token_class
						);
				
				if (flag)
					mode = 2;
				
				return flag;
			}
	);
	
	private static final ConditionNode NOT = new ConditionNode(
			"Not",
			(lexeme, token_class) -> {
				boolean flag =
						mode != 1 &&
						Resources.NOT.parse(
								lexeme,
								token_class
						);
				
				if (flag)
					mode = 2;
				
				return flag;
			}
	);
	
	public static void initialize() {
		Resources.error_index.put(
				new String[] {
					"Integer",
					"Real",
					"String",
					"Not",
					"Boolean",
					"Identifier",
					"Open Parenthesis"
				},
				0
		);

		Resources.error_index.put(
				new String[] {
					"Arithmetic Operator",
					"Relational Operator",
					"Boolean Conditional Operator"
				},
				1
		);
		
		
		declare.set(() -> INode.stack(
				"Assignment.Declare",
				Resources.ASSIGNMENT,
				new OrNode(content, group)
		));
		
		group.set(() -> {
			mode = 0;
			
			return INode.stack(
					"Assignment.Group",
					Resources.OPEN_PARENTHESIS,
					new OrNode(content, group),
					Resources.CLOSE_PARENTHESIS,
					new OrNode(
							arithmetic_number,
							relational_number,
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
							),
							null
					)
			);
		});
		
		content.set(() -> {
			mode = 0;
			
			return INode.stack(
					"Assignment.Content",
					new OrNode(
							content_number,
							content_string,
							content_boolean,
							content_identifier
					)
			);
		});
		
		content_number.set(() -> INode.stack(
				"Assignment.Content.Integer",
				GenericLogic.NUMBER,
				new OrNode(
						arithmetic_number,
						relational_number,
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
										GenericLogic.IDENTIFIER_OR_CALLABLE
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
				GenericLogic.IDENTIFIER_OR_CALLABLE,
				new OrNode(
						arithmetic_number,
						relational_number,
						relational_boolean,
						relational_string,
						relational_identifier,
						null
				)
		));
		
		arithmetic_number.set(() -> INode.stack(
				"Assignment.Arithmetic.Number",
				ARITHMETIC_OPERATOR,
				new OrNode(
						GenericLogic.NUMBER,
						GenericLogic.IDENTIFIER_OR_CALLABLE,
						ArithmeticLogic.group_number
				),
				new OrNode(ArithmeticLogic.content_number, null)
		));
		
		relational_number.set(() -> INode.stack(
				"Assignment.Conditional.Integer",
				RELATIONAL_OPERATOR,
				new OrNode(
						GenericLogic.NUMBER,
						GenericLogic.IDENTIFIER_OR_CALLABLE
				),
				new OrNode(
						ArithmeticLogic.content_number,
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
						GenericLogic.IDENTIFIER_OR_CALLABLE
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
						GenericLogic.IDENTIFIER_OR_CALLABLE
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
						GenericLogic.IDENTIFIER_OR_CALLABLE
				),
				new OrNode(
						BooleanExpressionLogic.conditional_operator,
						null
				)
		));
	}
}
