package logic;

import main.*;

public abstract class IfThenElseLogic {
	public static int parse(Scanner scanner, String lexeme, String token_class) {
		if (scanner.modeEmpty())
			return 0;
		
		switch (scanner.peekMode()) {
		case If0:
			if (!token_class.equals("open parenthesis")) {
				scanner.print_error(14);
				return 2;
			}
			
			scanner.popMode();
			scanner.pushMode(ScanMode.If1);
			scanner.pushMode(ScanMode.Expression0);
			return 1;
		case If1:
			if (!lexeme.equals("then")) {
				scanner.print_error(14);
				return 2;
			}
			
			scanner.popMode();
			scanner.pushMode(ScanMode.If2);
			return 1;
		case If2:
			if (lexeme.equals("begin")) {
				scanner.popMode();
				scanner.pushMode(ScanMode.If3, ScanMode.BodyDeclare1);
				return 1;
			}
			
			scanner.popMode();
			scanner.pushMode(ScanMode.Else0);
			return BodyLogic.bodyDeclare1(scanner, lexeme, token_class);
		case If3:
			// Expecting semicolon or `else` token.
			if (token_class.equals("semicolon")) {
				scanner.popMode();
				return 1;
			}
			
			if (lexeme.equals("else")) {
				scanner.popMode();
				scanner.pushMode(ScanMode.Else1);
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case Else0:
			// Optional `else` token.
			scanner.popMode();
			
			if (lexeme.equals("else")) {
				scanner.pushMode(ScanMode.Else1);
				return 1;
			}
			
			return BodyLogic.bodyDeclare1(scanner, lexeme, token_class);
		case Else1:
			scanner.popMode();
			
			if (lexeme.equals("begin")) {
				scanner.pushMode(ScanMode.Semicolon, ScanMode.BodyDeclare1);
				return 1;
			}
			
			return BodyLogic.bodyDeclare1(scanner, lexeme, token_class);
		default:
			return 0;
		}
	}
}
