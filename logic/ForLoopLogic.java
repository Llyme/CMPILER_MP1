package logic;

import java.util.Stack;

import identifier.*;
import main.*;

public abstract class ForLoopLogic {
	private static Stack<IIdentifier> counters = new Stack<IIdentifier>();
	private static Stack<Integer> maximums = new Stack<Integer>();
	
	public static int parse(Scanner scanner, String lexeme, String token_class) {
		if (scanner.modeEmpty())
			return 0;
		
		switch (scanner.peekMode()) {
		case ForLoop0:
			// Expecting identifier.
			if (!token_class.equals("identifier")) {
				scanner.print_error(0);
				return 2;
			}
			
			IIdentifier counter = scanner.getIdentifier(lexeme);
			
			if (counter == null)
				counter = new IdentifierInteger(
					"lexeme",
					false,
					0
				);
			
			counters.push(counter);
			scanner.popMode();
			scanner.pushMode(ScanMode.ForLoop1);
			return 1;
		case ForLoop1:
			// Expecting assignment.
			if (!token_class.equals("assignment")) {
				scanner.print_error(25);
				return 2;
			}
			
			scanner.popMode();
			scanner.pushMode(ScanMode.ForLoop2);
			return 1;
		case ForLoop2:
			// Expecting integer.
			switch (token_class) {
			case "integer":
				if (!counters.peek().isValid(lexeme)) {
					scanner.print_error(26);
					return 2;
				}
				
				counters.peek().setValue(lexeme);
				scanner.popMode();
				scanner.pushMode(ScanMode.ForLoop3);
				return 1;
			}
			
			return 1;
		case ForLoop3:
			// Expecting `to` token.
			if (!lexeme.equals("to")) {
				scanner.print_error(14);
				return 2;
			}
			
			scanner.popMode();
			scanner.pushMode(ScanMode.ForLoop4);
			return 1;
		case ForLoop4:
			// Expecting integer.
			if (!token_class.equals("integer")) {
				scanner.print_error(14);
				return 2;
			}
			
			maximums.push(Integer.parseInt(lexeme));
			scanner.popMode();
			scanner.pushMode(ScanMode.ForLoop5);
			return 1;
		case ForLoop5:
			// Expecting `do` token.
			if (!lexeme.equals("do")) {
				scanner.print_error(14);
				return 2;
			}
			
			scanner.popMode();
			scanner.pushMode(ScanMode.ForLoop6);
			return 1;
		case ForLoop6:
			scanner.popMode();
			
			if (lexeme.equals("begin")) {
				scanner.pushMode(ScanMode.ForLoop7);
				scanner.pushMode(ScanMode.BodyDeclare1);
				return 1;
			}
			
			return BodyLogic.bodyDeclare1(scanner, lexeme, token_class);
		case ForLoop7:
			if (!token_class.equals("semicolon")) {
				scanner.print_error(3);
				return 2;
			}
			
			counters.pop();
			maximums.pop();
			scanner.popMode();
			return 1;
		default:
			return 0;
		}
	}
}
