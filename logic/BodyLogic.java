package logic;

import java.util.ArrayList;
import java.util.Queue;

import identifier.*;
import main.*;
import node.*;

public abstract class BodyLogic {
	public static final PackageNode declare = new PackageNode();
	public static final PackageNode declare_semicolon = new PackageNode();
	
	public static final PackageNode content = new PackageNode();
	public static final PackageNode content_recursive = new PackageNode();
	
	public static final PackageNode identifier = new PackageNode();
	
	public static final PackageNode call = new PackageNode();
	public static final PackageNode call_param = new PackageNode();
	public static final PackageNode call_param_comma = new PackageNode();
	
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
				
				// Include 1 pair before this.
				INode.record(1),
				
				new OrNode(call, AssignmentLogic.declare),
				Resources.SEMICOLON,
				
				INode.a(() -> interpret())
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
	
	private static void interpret() {
		LexemeTokenPair[] pairs = Interpreter.flush();
		Identifier identifier =
				GenericLogic.getIdentifier(pairs[0].lexeme());
		
		if (identifier == null)
			return;
		
		if (interpret_call(pairs, identifier))
			;
		else if (interpret_assignment(pairs, identifier))
			;
	}
	
	private static boolean interpret_assignment
	(LexemeTokenPair[] pairs, Identifier identifier) {
		if (!pairs[1].token().equals("assignment"))
			return false;
		
		switch (identifier.getDataType()) {
		case "integer":
			Queue<LexemeTokenPair> queue0 = Helper.arithmeticPostfix(Helper.subsequence(
					pairs,
					2,
					pairs.length - 3
			));
			
			if (queue0 == null) {
				Interpreter.error("Invalid arithmetic expression!");
				return true;
			}
			
			identifier.setValue(Helper.solveInteger(queue0));
			return true;
		case "real":
			Queue<LexemeTokenPair> queue1 = Helper.arithmeticPostfix(Helper.subsequence(
					pairs,
					2,
					pairs.length - 3
			));
			
			if (queue1 == null) {
				Interpreter.error("Invalid arithmetic expression!");
				return true;
			}
			
			identifier.setValue(Helper.solveFloat(queue1));
			return true;
		case "boolean":
			Queue<LexemeTokenPair> queue2 = Helper.boolPostfix(Helper.subsequence(
					pairs,
					2,
					pairs.length - 3
			));
			
			if (queue2 == null) {
				Interpreter.error("Invalid boolean expression!");
				return true;
			}

			identifier.setValue(Helper.solveBool(queue2));
			
			return true;
		case "string":
			identifier.setValue(pairs[2].lexeme());
			return true;
		case "character":
			identifier.setValue(pairs[2].lexeme());
			return true;
		}
		
		Interpreter.error("Data type `" + identifier.getDataType() + "` is immutable!");
		return true;
	}
	
	private static boolean interpret_call
	(LexemeTokenPair[] pairs, Identifier identifier) {
		if (!pairs[1].lexeme().equals("("))
			return false;
		
		if (!identifier.getDataType().equals("procedure") &&
			!identifier.getDataType().equals("function")) {
			Interpreter.error(
					"Identifier `" +
					identifier.getName() +
					"` is not callable!"
			);
			return true;
		}
		
		
		// Get parameters.
		
		IdentifierProcedure procedure = (IdentifierProcedure) identifier;
		ArrayList<LexemeTokenPair[]> parameters = GenericLogic.getParameters(
				pairs,
				2,
				pairs.length - 2
		);
		
		// Record.
		
		Interpreter.record(() -> {
			if (specialProcedure(procedure, parameters))
				return;
			
			if (!GenericLogic.checkParameters(procedure, parameters))
				return;
			
			loadProcedure(procedure, parameters);
		});
		
		 return true;
	}
	
	/**
	 * Procedures that have special functionalities.
	 */
	private static boolean specialProcedure
	(IdentifierProcedure procedure, ArrayList<LexemeTokenPair[]> parameters) {
		IdentifierCollection identifiers = Interpreter.identifiers();
		
		switch (procedure.getName()) {
		case "write":
			return true;
		case "writeln":
			String result = "";
			
			for (LexemeTokenPair[] pairs : parameters) {
				switch (pairs[0].token()) {
				case "identifier":
					Identifier identifier = identifiers.get(pairs[0].lexeme());
					Object value = identifier.getValue();
					
					if (value == null)
						result += "null";
					else
						result += value.toString();
					
					break;
				case "string":
					String lexeme = pairs[0].lexeme();
					result += lexeme.substring(1, lexeme.length() - 1);
					break;
				case "integer":
					result += pairs[0].lexeme();
					break;
				case "real":
					result += pairs[0].lexeme();
					break;
				case "boolean":
					result += pairs[0].lexeme();
					break;
				}
			}
			
			MainWindow.appendConsoleText(result);
			return true;
		case "read":
			return true;
		case "readln":
			Interpreter.feed.peek().push(() -> {
				Identifier identifier = identifiers.get(parameters.get(0)[0].lexeme());
				String input = Interpreter.input;
				
				if (identifier.getDataType().equals("string"))
					input = "'" + input + "'";
				
				identifier.setValue(input);
			});
			Interpreter.read();
			
			return true;
		}
		
		return false;
	}
	
	private static void loadProcedure
	(IdentifierProcedure procedure, ArrayList<LexemeTokenPair[]> parameters) {
		IdentifierCollection identifiers = Interpreter.identifiers();
		identifiers.pushScope();
		
		
		// Populate scoped identifiers.
		
		for (int i = 0; i < procedure.parameterLength(); i++) {
			Identifier identifier0 = null;
			
			switch (procedure.getDataType(i)) {
			case "boolean":
				identifiers.addToScope(identifier0 = new IdentifierBoolean(
						procedure.getIdentifier(i),
						false
				));
				break;
			case "character":
				identifiers.addToScope(identifier0 = new IdentifierCharacter(
						procedure.getIdentifier(i),
						false
				));
				break;
			case "integer":
				identifiers.addToScope(identifier0 = new IdentifierInteger(
						procedure.getIdentifier(i),
						false
				));
				break;
			case "real":
				identifiers.addToScope(identifier0 = new IdentifierReal(
						procedure.getIdentifier(i),
						false
				));
				break;
			case "string":
				identifiers.addToScope(identifier0 = new IdentifierString(
						procedure.getIdentifier(i),
						false
				));
				break;
			}
			
			
			// Construct scoped identifiers.
			
			callParameters(parameters.get(i), identifier0);
		}
		
		identifiers.popScope();
	}
	
	private static void callParameters(LexemeTokenPair[] pairs, Identifier receiver) {
		if (callParameters_function(pairs, receiver))
			;
	}
	
	private static boolean callParameters_function
	(LexemeTokenPair[] pairs, Identifier receiver) {
		if (pairs.length <= 1 ||
			!pairs[0].token().equals("identifier") ||
			!pairs[1].lexeme().equals("("))
			return false;
		
		Identifier identifier = GenericLogic.getIdentifier(pairs[0].lexeme());
		
		if (identifier == null)
			return true;
		
		if (!identifier.getDataType().equals("function")) {
				Interpreter.error(
						"Identifier `" +
						identifier.getName() +
						"` is not a function!"
				);
				return true;
			}
			
			
		// Get parameters.
		
		IdentifierProcedure procedure = (IdentifierProcedure) identifier;
		ArrayList<LexemeTokenPair[]> parameters = GenericLogic.getParameters(
				pairs,
				1,
				pairs.length - 2
		);
		
		if (specialProcedure(procedure, parameters))
			return true;
		
		if (!GenericLogic.checkParameters(procedure, parameters))
			return true;
		
		loadProcedure(procedure, parameters);
		return true;
	}
}
