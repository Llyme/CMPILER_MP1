package logic;

import java.util.Stack;

import identifier.*;
import main.*;

public abstract class ExpressionLogic {
	private static Stack<IIdentifier> identifiers = new Stack<IIdentifier>();
	private static Stack<String> relations = new Stack<String>();
	private static Boolean result = false;
	private static Boolean not = false;
	
	public static int parse(Scanner scanner, String lexeme, String token_class) {
		if (scanner.modeEmpty())
			return 0;
		
		switch (scanner.peekMode()) {
		case Expression0:
			// Expecting `not` boolean operator,
			// identifier, boolean, or open parenthesis.
			switch (lexeme) {
			case "not":
				if (not) {
					scanner.print_error(14);
					return 2;
				}
				
				not = true;
				return 1;
			}
			
			switch (token_class) {
			case "identifier":
				/*IIdentifier identifier = scanner.getIdentifier(lexeme);
				
				if (identifier == null) {
					scanner.print_error(15);
					return 2;
				}*/
				
				if (not) {
					/*if (identifier.getDataType() != "boolean") {
						scanner.print_error(16);
						return 2;
					}
					
					identifiers.push(new IdentifierBoolean(
						null,
						false,
						!(Boolean)identifier.getValue()
					));*/
					not = false;
				} /*else
					identifiers.push(identifier);*/
				
				scanner.popMode();
				scanner.pushMode(ScanMode.Expression1);
				return 1;
			case "open parenthesis":
				scanner.popMode();
				scanner.pushMode(ScanMode.Expression3, ScanMode.Expression0);
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case Expression1:
			// Expecting relational operator.
			if (!token_class.equals("relational operator")) {
				scanner.print_error(14);
				return 2;
			}

			relations.push(lexeme);
			scanner.popMode();
			scanner.pushMode(ScanMode.Expression2);
			return 1;
		case Expression2:
			// Expecting identifier.
			switch (token_class) {
			case "identifier":
			case "integer":
			case "real":
			case "predeclared":
			case "string":
				/*IIdentifier identifier = scanner.getIdentifier(lexeme);
				
				if (identifier == null) {
					scanner.print_error(15);
					return 2;
				}
				
				if (identifier.getDataType() != identifiers.peek().getDataType()) {
					scanner.print_error(16);
					return 2;
				}*/
				
				// Do relation maths here.
				//identifiers.pop();
				relations.pop();
				scanner.popMode();
				scanner.pushMode(ScanMode.Expression3);
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case Expression3:
			// Expecting boolean operator or close parenthesis.
			switch (token_class) {
			case "boolean operator":
				if (lexeme.equals("not")) {
					scanner.print_error(14);
					return 2;
				}
				
				scanner.popMode();
				scanner.pushMode(ScanMode.Expression0);
				return 1;
			case "close parenthesis":
				scanner.popMode();
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		default:
			return 0;
		}
	}
}
