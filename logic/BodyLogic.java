package logic;

import java.util.Stack;

import identifier.IIdentifier;
import identifier.IdentifierProcedure;
import main.*;

public abstract class BodyLogic {
	public static ChainPredicate group;
	public static ChainPredicate assignment;
	public static ChainPredicate expression;
	public static ChainPredicate expression_negate;
	public static ChainPredicate expression_operator;
	public static ChainPredicate expression_boolean;
	public static ChainPredicate expression_call;
	public static ChainPredicate expression_call_param;
	public static ChainPredicate expression_call_param_comma;
	
	private static int procedureParameterIndex = 0;
	
	private static Stack<Object> chains = new Stack<Object>();
	
	public static void initialize() {
		assignment = () -> new Object[] {
			Chain2.or(expression, group, expression_negate),
			Resources.SEMICOLON
		};
		
		expression = () -> new Object[] {
			Resources.RELATIONAL_VALUE,
			Chain2.or(expression_call, expression_operator, expression_boolean, null)
		};
		
		expression_negate = () -> new Object[] {
			Resources.NOT,
			Chain2.or(group, Resources.RELATIONAL_VALUE),
		};
		
		expression_operator = () -> new Object[] {
			Resources.RELATIONAL_OPERATOR,
			Chain2.or(Resources.RELATIONAL_VALUE, expression_negate),
			Chain2.or(null, expression_boolean)
		};
		
		expression_boolean = () -> new Object[] {
			Resources.BOOLEAN_OPERATOR,
			Chain2.or(expression, group, expression_negate)
		};
		
		expression_call = () -> new Object[] {
				Resources.OPEN_PARENTHESIS,
				Chain2.or(expression_call_param, null),
				Resources.CLOSE_PARENTHESIS
		};
		
		expression_call_param = () -> new Object[] {
				Resources.RELATIONAL_VALUE,
				Chain2.or(expression_call_param_comma, null)
		};
		
		expression_call_param_comma = () -> new Object[] {
				Resources.COMMA,
				Resources.RELATIONAL_VALUE,
				Chain2.or(expression_call_param_comma, null)
		};
		
		group = () -> new Object[] {
			Resources.OPEN_PARENTHESIS,
			Chain2.or(expression, group, expression_negate),
			Resources.CLOSE_PARENTHESIS,
			Chain2.or(null, expression_boolean)
		};
	}
	
	public static int chainParse(Scanner scanner, String lexeme, String token_class) {
		if (chains.empty())
			return 0;
		
		Object item = chains.peek();
		
		if (item instanceof Condition condition) {
			if (condition.fire(lexeme, token_class)) {
				chains.pop();
				return 1;
			}
			
			scanner.print_error(-1);
			return 2;
			
		} else if (item instanceof Object[] nested) {
			if (nested == null || nested.length == 0) {
				chains.pop();
				return chainParse(scanner, lexeme, token_class);
			}
			
			
			// Unpack first level predicates.
			
			for (int i = 0; i < nested.length; i++) {
				Object item0 = nested[i];
				
				if (item0 instanceof ChainPredicate predicate)
					nested[i] = predicate.fire();
			}
			
			
			// Try each array's first item (has to be a condition.)
			
			boolean flag = false;
			
			for (int i = 0; i < nested.length; i++) {
				if (nested[i] == null) {
					// `null` means its not required.
					flag = true;
					continue;
				}

				if (nested[i] instanceof Object[] item0) {
					Condition condition = (Condition)item0[0];
					
					if (condition.fire(lexeme, token_class)) {
						chains.pop();
						
						for (int n = item0.length - 1; n > 0; n--)
							chains.push(item0[n]);
						
						return 1;
					}
				} else if (nested[i] instanceof Condition condition) {
					if (condition.fire(lexeme, token_class)) {
						chains.pop();
						
						return 1;
					}
				}
			}
			
			if (flag) {
				chains.pop();
				return chainParse(scanner, lexeme, token_class);
			}
			
			scanner.print_error(-1);
			return 2;
			
		} else if (item instanceof ChainPredicate predicate) {
			Object[] array = predicate.fire();
			chains.pop();
			
			for (int i = array.length - 1; i >= 0; i--)
				chains.push(array[i]);
			
			return chainParse(scanner, lexeme, token_class);
		}
		
		return 0;
	}
	
	public static int bodyDeclare1(Scanner scanner, String lexeme, String token_class) {
		switch (lexeme) {
		case "for":
			scanner.pushMode(ScanMode.ForLoop0);
			return 1;
		case "if":
			scanner.pushMode(ScanMode.If0);
			return 1;
		}
		
		switch (token_class) {
		case "identifier":
		case "predeclared":
			/*if (scanner.getIdentifier(lexeme) == null) {
				scanner.print_error(15);
				return 2;
			}

			IIdentifier identifier = scanner.getIdentifier(lexeme);
			
			if (identifier == null) {
				scanner.print_error(15);
				return 2;
			}
			
			scanner.setTargetIdentifier(identifier);*/
			scanner.pushMode(ScanMode.BodyIdentifier0);
			return 1;
		}
		
		scanner.print_error(14);
		return 2;
	}
	
	public static int parse(Scanner scanner, String lexeme, String token_class) {
		int chainParseReturn = chainParse(scanner, lexeme, token_class);
		
		if (chainParseReturn != 0)
			return chainParseReturn;
		
		if (scanner.modeEmpty()) {
			if (lexeme.equals("begin")) {
				scanner.setDeclarationType(DeclarationType.MainProgram);
				scanner.pushMode(ScanMode.Dot, ScanMode.BodyDeclare1);
				return 1;
			}
			
			return 0;
		}
		
		switch (scanner.peekMode()) {
		case BodyDeclare0:
			// Expecting a `begin` token.
			if (!lexeme.equals("begin")) {
				scanner.print_error(13);
				return 2;
			}

			scanner.popMode();
			scanner.pushMode(ScanMode.BodyDeclare1);
			return 1;
		case BodyDeclare1:
			// Expecting something to do.
			switch (lexeme) {
			case "end":
				if (!scanner.procedureStackEmpty())
					scanner.popProcedureStack();
				
				scanner.popMode();
				return 1;
			}
			
			return bodyDeclare1(scanner, lexeme, token_class);
		case BodyIdentifier0:
			// Expecting an identifier modifier.
			switch (token_class) {
			case "assignment":
				scanner.popMode();
				chains.push(assignment);
				//scanner.pushMode(ScanMode.BodyAssignment0);
				return 1;
			case "open parenthesis":
				/*IIdentifier identifier = scanner.getTargetIdentifier();
				
				if (identifier.getDataType() != "function" &&
					identifier.getDataType() != "procedure") {
					scanner.print_error(23);
					return 2;
				}
				
				procedureParameterIndex = 0;
				scanner.setTargetProcedure((IdentifierProcedure)identifier);*/
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyProcedure0);
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case BodyAssignment0:
			// Expecting an identifier, predeclared, integer, or open parenthesis.
			switch (token_class) {
			case "identifier":
			case "predeclared":
				/*IIdentifier identifier = scanner.getIdentifier(lexeme);
				
				if (identifier == null) {
					scanner.print_error(15);
					return 2;
				}
				
				if (identifier.getDataType() != scanner.getTargetIdentifier().getDataType()) {
					scanner.print_error(16);
					return 2;
				}*/

				scanner.popMode();
				scanner.pushMode(ScanMode.BodyAssignment1);
				return 1;
			case "integer":
				/*if (!scanner.getTargetIdentifier().getDataType().equals("integer")) {
					scanner.print_error(16);
					return 2;
				}*/

				scanner.popMode();
				scanner.pushMode(ScanMode.BodyAssignment1);
				return 1;
			case "real":
				/*if (!scanner.getTargetIdentifier().getDataType().equals("real")) {
					scanner.print_error(16);
					return 2;
				}*/

				scanner.popMode();
				scanner.pushMode(ScanMode.BodyAssignment1);
				return 1;
			case "open parenthesis":
				/*String dataType = scanner.getTargetIdentifier().getDataType();
				
				if (!dataType.equals("integer") &&
					!dataType.equals("real")) {
					scanner.print_error(16);
					return 2;
				}*/

				scanner.popMode();
				scanner.pushMode(
					ScanMode.Arithmetic1,
					ScanMode.ArithmeticGroup0
				);
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case BodyAssignment1:
			// Expecting an operator.
			switch (token_class) {
			case "arithmetic operator":
				String dataType = scanner.getTargetIdentifier().getDataType();
				if (dataType != "integer" &&
					dataType != "real") {
					scanner.print_error(17);
					return 2;
				}

				scanner.popMode();
				scanner.pushMode(
					ScanMode.Arithmetic0
				);
				return 1;
			case "semicolon":
				scanner.popMode();
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case BodyProcedure0:
			// Expecting an identifier, a literal, or a close parenthesis.
			IdentifierProcedure procedure0 = scanner.getTargetProcedure();
			
			switch (token_class) {
			case "identifier":
			case "predeclared":
			case "literal":
				/*if (!procedure0.hasVarargs() &&
					procedureParameterIndex > procedure0.parameterCount() - 1) {
					scanner.print_error(24);
					return 2;
				}
				
				String type = procedure0.getParameterType(procedureParameterIndex);
				
				if (token_class.equals("literal")) {
					if (!type.isEmpty() && type != "string") {
						scanner.print_error(16);
						return 2;
					}
				}
				else {
					IIdentifier identifier = scanner.getIdentifier(lexeme);
					
					if (identifier == null) {
						scanner.print_error(15);
						return 2;
					}
					
					if (!type.isEmpty() && identifier.getDataType() != type) {
						scanner.print_error(16);
						return 2;
					}
				}*/
				
				procedureParameterIndex++;
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyProcedure1);
				return 1;
			case "close parenthesis":
				if (procedure0.hasVarargs()) {
					if (procedureParameterIndex < procedure0.parameterCount()) {
						scanner.print_error(24);
						return 2;
					}
				}
				else if (procedureParameterIndex != procedure0.parameterCount()) {
					scanner.print_error(24);
					return 2;
				}
				
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyProcedure3);
				return 1;
			}

			scanner.print_error(14);
			return 2;
		case BodyProcedure1:
			// Expecting a comma or a close parenthesis.
			IdentifierProcedure procedure1 = scanner.getTargetProcedure();
			
			switch (token_class) {
			case "comma":
				/*if (!procedure1.hasVarargs() && procedureParameterIndex >= procedure1.parameterCount()) {
					scanner.print_error(24);
					return 2;
				}*/
				
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyProcedure2);
				return 1;
			case "close parenthesis":
				/*if (procedure1.hasVarargs()) {
					if (procedureParameterIndex < procedure1.parameterCount()) {
						scanner.print_error(24);
						return 2;
					}
				} else if (procedureParameterIndex != procedure1.parameterCount()) {
					scanner.print_error(24);
					return 2;
				}*/
				
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyProcedure3);
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case BodyProcedure2:
			// Expecting an identifier or literal.

			/*IdentifierProcedure procedure2 = scanner.getTargetProcedure();
			
			if (!procedure2.hasVarargs() &&
				procedureParameterIndex > procedure2.parameterCount() - 1) {
				scanner.print_error(24);
				return 2;
			}
			
			String type = procedure2.getParameterType(procedureParameterIndex);
			
			switch(token_class) {
			case "identifier":
			case "predeclared":
				IIdentifier identifier = scanner.getIdentifier(lexeme);
				
				if (identifier == null) {
					scanner.print_error(15);
					return 2;
				}
				
				if (!type.isEmpty() && identifier.getDataType() != type) {
					scanner.print_error(16);
					return 2;
				}
				
				break;
			case "literal":
				if (!type.isEmpty() && type != "string") {
					scanner.print_error(16);
					return 2;
				}
				
				break;
			case "integer":
				if (!type.isEmpty() && type != "integer") {
					scanner.print_error(16);
					return 2;
				}
				
				break;
			case "real":
				if (!type.isEmpty() && type != "real") {
					scanner.print_error(16);
					return 2;
				}
				
				break;
			default:
				scanner.print_error(0);
				return 2;
			}
			
			procedureParameterIndex++;*/
			
			switch (token_class) {
			case "identifier":
			case "predeclared":
			case "literal":
			case "integer":
			case "real":
				break;
			default:
				scanner.print_error(0);
				return 2;
			}
			
			scanner.popMode();
			scanner.pushMode(ScanMode.BodyProcedure1);
			return 1;
		case BodyProcedure3:
			// Expecting a semicolon.
			if (!token_class.equals("semicolon")) {
				scanner.print_error(3);
				return 2;
			}
			
			if (scanner.getBodyType() == BodyType.Main) {
				scanner.pushProcedureStack(scanner.getTargetProcedure());
				scanner.setTargetProcedure(null);
			}
			
			scanner.popMode();
			return 1;
		default:
			return 0;
		}
	}
}
