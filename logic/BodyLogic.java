package logic;

import identifier.IIdentifier;
import identifier.IdentifierProcedure;
import main.*;

public abstract class BodyLogic {
	private static int procedureParameterIndex = 0;
	
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
			scanner.pushMode(ScanMode.BodyIdentifier0);
			return 1;
		}
		
		scanner.print_error(14);
		return 2;
	}
	
	public static int parse(Scanner scanner, String lexeme, String token_class) {
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
				scanner.pushMode(ScanMode.BodyAssignment0);
				return 1;
			case "open parenthesis":
				IIdentifier identifier = scanner.getTargetIdentifier();
				
				if (identifier.getDataType() != "function" &&
					identifier.getDataType() != "procedure") {
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
			// Expecting an identifier, predeclared, integer, or open parenthesis.
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
				scanner.pushMode(ScanMode.BodyAssignment1);
				return 1;
			case "integer":
				if (scanner.getTargetIdentifier().getDataType() != "integer") {
					scanner.print_error(16);
					return 2;
				}

				scanner.popMode();
				scanner.pushMode(ScanMode.BodyAssignment1);
				return 1;
			case "open parenthesis":
				if (scanner.getTargetIdentifier().getDataType() != "integer") {
					scanner.print_error(16);
					return 2;
				}

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
				if (scanner.getTargetIdentifier().getDataType() != "integer") {
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
				if (!procedure0.hasVarargs() &&
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
				}
				
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
				if (!procedure1.hasVarargs() && procedureParameterIndex >= procedure1.parameterCount()) {
					scanner.print_error(24);
					return 2;
				}
				
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyProcedure2);
				return 1;
			case "close parenthesis":
				if (procedure1.hasVarargs()) {
					if (procedureParameterIndex < procedure1.parameterCount()) {
						scanner.print_error(24);
						return 2;
					}
				} else if (procedureParameterIndex != procedure1.parameterCount()) {
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
			// Expecting an identifier or literal.
			
			switch(token_class) {
			case "identifier":
			case "predeclared":
			case "literal":
				break;
			default:
				scanner.print_error(0);
				return 2;
			}
			
			IdentifierProcedure procedure2 = scanner.getTargetProcedure();
			
			if (!procedure2.hasVarargs() &&
				procedureParameterIndex > procedure2.parameterCount() - 1) {
				scanner.print_error(24);
				return 2;
			}
			
			String type = procedure2.getParameterType(procedureParameterIndex);
			
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
