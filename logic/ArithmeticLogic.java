package logic;

import identifier.IIdentifier;
import main.*;

public abstract class ArithmeticLogic {
	public static int parse(Scanner scanner, String lexeme, String token_class) {
		if (scanner.modeEmpty())
			return 0;
		
		switch (scanner.peekMode()) {
		case Arithmetic0:
			// Expecting an identifier, a number, or an open parenthesis.
			switch (token_class) {
			case "identifier":
			case "predeclared":
				IIdentifier identifier = scanner.getIdentifier(lexeme);
				
				if (identifier == null) {
					scanner.print_error(15);
					return 2;
				}
				
				if (identifier.getDataType() != scanner.getTargetIdentifier().getDataType()) {
					scanner.print_error(16);
					return 2;
				}

				scanner.popMode();
				scanner.pushMode(ScanMode.Arithmetic1);
				return 1;
			case "integer":
				if (scanner.getTargetIdentifier().getDataType() != "integer") {
					scanner.print_error(16);
					return 2;
				}

				scanner.popMode();
				scanner.pushMode(ScanMode.Arithmetic1);
				return 1;
			case "open parenthesis":
				scanner.popMode();
				scanner.pushMode(
					ScanMode.Arithmetic1,
					ScanMode.ArithmeticGroup0
				);
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case Arithmetic1:
			// Expecting an arithmetic operator.
			switch (token_class) {
			case "arithmetic operator":
				scanner.popMode();
				scanner.pushMode(ScanMode.Arithmetic0);
				return 1;
			case "semicolon":
				scanner.popMode();
				return 1;
			}
			
			scanner.print_error(18);
			return 2;
		case ArithmeticGroup0:
			switch (token_class) {
			case "identifier":
			case "predeclared":
				IIdentifier identifier = scanner.getIdentifier(lexeme);
				
				if (identifier == null) {
					scanner.print_error(15);
					return 2;
				}
				
				if (identifier.getDataType() != scanner.getTargetIdentifier().getDataType()) {
					scanner.print_error(16);
					return 2;
				}

				scanner.popMode();
				scanner.pushMode(ScanMode.ArithmeticGroup1);
				return 1;
			case "integer":
				if (scanner.getTargetIdentifier().getDataType() != "integer") {
					scanner.print_error(16);
					return 2;
				}

				scanner.popMode();
				scanner.pushMode(ScanMode.ArithmeticGroup1);
				return 1;
			case "open parenthesis":
				scanner.popMode();
				scanner.pushMode(
					ScanMode.ArithmeticGroup1,
					ScanMode.ArithmeticGroup0
				);
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case ArithmeticGroup1:
			switch (token_class) {
			case "close parenthesis":
				scanner.popMode();
				return 1;
			case "arithmetic operator":
				scanner.popMode();
				scanner.pushMode(ScanMode.ArithmeticGroup0);
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		default:
			return 0;
		}
	}
}
