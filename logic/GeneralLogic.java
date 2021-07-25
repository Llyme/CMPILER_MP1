package logic;

import main.*;

public abstract class GeneralLogic {
	public static int parse(Scanner scanner, String lexeme, String token_class) {
		if (scanner.modeEmpty())
			return 0;
		
		switch (scanner.peekMode()) {
		case Semicolon:
			if (!token_class.equals("semicolon")) {
				scanner.print_error(3);
				return 2;
			}
			
			scanner.popMode();
			return 1;
		case Dot:
			if (!token_class.equals("dot")) {
				scanner.print_error(19);
				return 2;
			}
			
			scanner.popMode();
			return 1;
		default:
			return 0;
		}
	}
}
