package logic;

import main.ScanMode;
import main.Scanner;

public abstract class ProgramLogic {
	public static int parse(Scanner scanner, String lexeme, String token_class) {
		if (scanner.getProgramName() == null) {
			// Entry
			if (scanner.modeEmpty()) {
				if (!lexeme.equals("program")) {
					scanner.print_error(1);
					return 2;
				}
				
				// Accept program token.
				scanner.pushMode(ScanMode.Program);
				return 1;
			}
				
			switch (scanner.peekMode()) {
			case Program:
				// Program token accepted. Expecting an identifier.
				if (!token_class.equals("identifier")) {
					scanner.print_error(0);
					return 2;
				}
				
				scanner.setProgramName(lexeme);
				return 1;
				
			default:
				scanner.print_error(2);
				return 2;
			}
			
			
		} else if (scanner.getProgramName() != null &&
				!scanner.modeEmpty() &&
				scanner.peekMode() == ScanMode.Program) {
			// Exit
			if (!token_class.equals("semicolon")) {
				scanner.print_error(3);
				return 2;
			}

			scanner.popMode();
			return 1;
		}
		
		return 0;
	}
}
