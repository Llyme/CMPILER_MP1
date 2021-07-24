package logic;

import identifier.IIdentifier;
import identifier.IdentifierProcedure;
import main.*;

public abstract class BodyLogic {
	private static int procedureParameterIndex = 0;
	
	public static int parse(Scanner scanner, String lexeme, String token_class) {
		if (scanner.modeEmpty()) {
			if (lexeme.equals("begin")) {
				scanner.setDeclarationType(DeclarationType.MainProgram);
				scanner.pushMode(ScanMode.BodyDeclare1);
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
			if (lexeme.equals("end")) {
				scanner.popMode();
				
				if (!scanner.procedureStackEmpty())
					scanner.popProcedureStack();
				else
					scanner.pushMode(ScanMode.BodyExit0);
				
				return 1;
			}
			
			if (lexeme.equals("readln")) {
				scanner.waitForUserInput();
				return 1;
			}
			
			if (lexeme.equals("writeln")) {
				// Do some printing here.
				return 1;
			}
			
			if (lexeme.equals("write")) {
				// Do some printing here.
				return 1;
			}
			
			if (token_class.equals("identifier")) {
				if (scanner.getIdentifier(lexeme) == null) {
					scanner.print_error(15);
					return 2;
				}

				IIdentifier identifier = scanner.getIdentifier(lexeme);
				
				if (identifier == null) {
					scanner.print_error(15);
					return 2;
				}
				
				scanner.setTargetIdentifier(identifier);
				
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyIdentifier0);
				return 1;
			}
			
			return 1;
		case BodyExit0:
			switch (scanner.getBodyType()) {
			case Main:
				if (!token_class.equals("dot")) {
					scanner.print_error(19);
					return 2;
				}
				
				scanner.popMode();
				return 1;
			default:
				if (!token_class.equals("semicolon")) {
					scanner.print_error(3);
					return 2;
				}
				
				scanner.setTargetProcedure(null);
				scanner.setBodyType(BodyType.Main);
				scanner.popMode();
				return 1;
			}
		case BodyIdentifier0:
			// Expecting an identifier modifier.
			switch (token_class) {
			case "assignment":
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyAssignment0);
				return 1;
			case "open parenthesis":
				IIdentifier identifier = scanner.getTargetIdentifier();
				
				if (identifier.getDataType() != "function") {
					scanner.print_error(23);
					return 2;
				}
				
				procedureParameterIndex = 0;
				scanner.setTargetProcedure((IdentifierProcedure)identifier);
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyProcedure0);
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case BodyAssignment0:
			// Expecting an identifier.
			if (token_class.equals("identifier")) {
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
				scanner.pushMode(ScanMode.BodyAssignment1);
				return 1;
			}
			
			if (token_class.equals("integer")) {
				if (scanner.getTargetIdentifier().getDataType() != "integer") {
					scanner.print_error(16);
					return 2;
				}

				scanner.popMode();
				scanner.pushMode(ScanMode.BodyAssignment1);
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case BodyAssignment1:
			// Expecting an operator.
			if (token_class.equals("arithmetic operator")) {
				if (scanner.getTargetIdentifier().getDataType() != "integer") {
					scanner.print_error(17);
					return 2;
				}

				scanner.popMode();
				scanner.pushMode(ScanMode.BodyArithmetic0);
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case BodyArithmetic0:
			// Expecting an identifier or number.
			if (token_class.equals("identifier")) {
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
				scanner.pushMode(ScanMode.BodyArithmetic1);
				return 1;
			}
			
			if (token_class.equals("integer")) {
				if (scanner.getTargetIdentifier().getDataType() != "integer") {
					scanner.print_error(16);
					return 2;
				}

				scanner.popMode();
				scanner.pushMode(ScanMode.BodyArithmetic1);
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case BodyArithmetic1:
			// Expecting an arithmetic operator or semicolon.
			if (token_class.equals("arithmetic operator")) {
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyArithmetic0);
				return 1;
			}
			
			if (token_class.equals("semicolon")) {
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyDeclare1);
				return 1;
			}
			
			scanner.print_error(18);
			return 2;
		case BodyProcedure0:
			// Expecting an identifier or a close parenthesis.
			IdentifierProcedure procedure0 = scanner.getTargetProcedure();
			
			switch (token_class) {
			case "identifier":
				if (procedureParameterIndex > procedure0.parameterCount() - 1) {
					scanner.print_error(24);
					return 2;
				}
				
				IIdentifier identifier = scanner.getIdentifier(lexeme);
				
				if (identifier == null) {
					scanner.print_error(15);
					return 2;
				}
				
				if (identifier.getDataType() != procedure0.getParameterType(procedureParameterIndex)) {
					scanner.print_error(16);
					return 2;
				}
				
				procedureParameterIndex++;
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyProcedure1);
				return 1;
			case "close parenthesis":
				if (procedureParameterIndex != procedure0.parameterCount()) {
					scanner.print_error(24);
					return 2;
				}
				
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case BodyProcedure1:
			// Expecting a comma or a close parenthesis.
			IdentifierProcedure procedure1 = scanner.getTargetProcedure();
			
			switch (token_class) {
			case "comma":
				if (procedureParameterIndex >= procedure1.parameterCount()) {
					scanner.print_error(24);
					return 2;
				}
				
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyProcedure2);
				return 1;
			case "close parenthesis":
				if (procedureParameterIndex != procedure1.parameterCount()) {
					scanner.print_error(24);
					return 2;
				}
				
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyProcedure3);
				return 1;
			}
			
			scanner.print_error(14);
			return 2;
		case BodyProcedure2:
			// Expecting an identifier.
			
			if (!token_class.equals("identifier")) {
				scanner.print_error(0);
				return 2;
			}
			
			IdentifierProcedure procedure2 = scanner.getTargetProcedure();
			
			if (procedureParameterIndex > procedure2.parameterCount() - 1) {
				scanner.print_error(24);
				return 2;
			}
			
			IIdentifier identifier = scanner.getIdentifier(lexeme);
			
			if (identifier == null) {
				scanner.print_error(15);
				return 2;
			}
			
			if (identifier.getDataType() != procedure2.getParameterType(procedureParameterIndex)) {
				scanner.print_error(16);
				return 2;
			}
			
			procedureParameterIndex++;
			scanner.popMode();
			scanner.pushMode(ScanMode.BodyProcedure1);
			return 1;
		case BodyProcedure3:
			// Expecting a semicolon.
			if (!token_class.equals("semicolon")) {
				scanner.print_error(3);
				return 2;
			}
			
			scanner.popMode();
			scanner.pushMode(ScanMode.BodyDeclare1);
			
			if (scanner.getBodyType() == BodyType.Main) {
				scanner.pushMode(ScanMode.BodyDeclare1);
				scanner.pushProcedureStack(scanner.getTargetProcedure());
				scanner.setTargetProcedure(null);
			}
			return 1;
		default:
			return 0;
		}
	}
}
