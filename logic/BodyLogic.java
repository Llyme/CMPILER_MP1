package logic;

import main.*;
import node.*;

public abstract class BodyLogic {
	public static PackageNode declare = new PackageNode();
	public static PackageNode declare_semicolon = new PackageNode();
	
	public static PackageNode content = new PackageNode();
	public static PackageNode content_recursive = new PackageNode();
	
	public static PackageNode identifier = new PackageNode();
	
	public static PackageNode group = new PackageNode();
	public static PackageNode assignment = new PackageNode();
	
	public static PackageNode expression = new PackageNode();
	public static PackageNode expression_negate = new PackageNode();
	public static PackageNode expression_operator = new PackageNode();
	public static PackageNode expression_boolean = new PackageNode();
	
	public static PackageNode call = new PackageNode();
	public static PackageNode call_param = new PackageNode();
	public static PackageNode call_param_comma = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
			"Body.Declare",
			Resources.BEGIN,
			new OrNode(content_recursive, null),
			Resources.END
		));
		
		declare_semicolon.set(() -> INode.stack(
			"Body.DeclareWithSemicolon",
			declare,
			Resources.SEMICOLON
		));
		
		
		content.set(() -> INode.stack(
			"Body.Content",
			new OrNode(
					ForLoopLogic.declare,
					IfThenElseLogic.declare,
					identifier
			)
		));
		
		content_recursive.set(() -> INode.stack(
			"Body.Content (Recursive)",
			content,
			new OrNode(content_recursive, null)
		));
		
		
		identifier.set(() -> INode.stack(
			"Body.Identifier",
			new OrNode(Resources.IDENTIFIER, Resources.PREDECLARED),
			new OrNode(call, assignment),
			Resources.SEMICOLON
		));
		
		assignment.set(() -> INode.stack(
			"Body.Assignment",
			Resources.ASSIGNMENT,
			new OrNode(expression, group, expression_negate)
		));
		
		
		expression.set(() -> INode.stack(
			"Body.Expression",
			Resources.RELATIONAL_VALUE,
			new OrNode(call, expression_operator, expression_boolean, null)
		));
		
		expression_negate.set(() -> INode.stack(
			"Body.Expression.Negate",
			Resources.NOT,
			new OrNode(group, Resources.RELATIONAL_VALUE)
		));
		
		expression_operator.set(() -> INode.stack(
			"Body.Expression.Operator",
			Resources.RELATIONAL_OPERATOR,
			new OrNode(Resources.RELATIONAL_VALUE, expression_negate),
			new OrNode(expression_boolean, null)
		));
		
		expression_boolean.set(() -> INode.stack(
			"Body.Expression.Boolean",
			Resources.BOOLEAN_OPERATOR,
			new OrNode(expression, group, expression_negate)
		));
		
		
		call.set(() -> INode.stack(
			"Body.Call",
			Resources.OPEN_PARENTHESIS,
			new OrNode(call_param, null),
			Resources.CLOSE_PARENTHESIS
		));
		
		call_param.set(() -> INode.stack(
			"Body.Call.Parameter",
			Resources.RELATIONAL_VALUE,
			new OrNode(call_param_comma, null)
		));
		
		call_param_comma.set(() -> INode.stack(
			"Body.Call.Parameter.Comma",
			Resources.COMMA,
			Resources.RELATIONAL_VALUE,
			new OrNode(call_param_comma, null)
		));
		
		
		group.set(() -> INode.stack(
			"Body.Group",
			Resources.OPEN_PARENTHESIS,
			new OrNode(expression, group, expression_negate),
			Resources.CLOSE_PARENTHESIS,
			new OrNode(expression_boolean, null)
		));
	}
}
