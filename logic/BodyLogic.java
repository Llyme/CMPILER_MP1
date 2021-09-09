package logic;

import main.*;
import node.*;

public abstract class BodyLogic {
	public static PackageNode declare = new PackageNode();
	public static PackageNode declare_semicolon = new PackageNode();
	
	public static PackageNode content = new PackageNode();
	public static PackageNode content_recursive = new PackageNode();
	
	public static PackageNode identifier = new PackageNode();
	
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
						WhileLoopLogic.declare,
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
				new OrNode(call, AssignmentLogic.declare),
				Resources.SEMICOLON
		));
		
		
		call.set(() -> INode.stack(
				"Body.Call",
				Resources.OPEN_PARENTHESIS,
				new OrNode(call_param, null),
				Resources.CLOSE_PARENTHESIS
		));
		
		call_param.set(() -> INode.stack(
				"Body.Call.Parameter",
				new OrNode(AssignmentLogic.content, AssignmentLogic.group),
				new OrNode(call_param_comma, null)
		));
		
		call_param_comma.set(() -> INode.stack(
				"Body.Call.Parameter.Comma",
				Resources.COMMA,
				new OrNode(AssignmentLogic.content, AssignmentLogic.group),
				new OrNode(call_param_comma, null)
		));
	}
}
