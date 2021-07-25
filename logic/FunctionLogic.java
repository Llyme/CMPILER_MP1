package logic;

import java.util.ArrayList;

import identifier.IdentifierFunction;
import main.BodyType;
import main.DeclarationType;
import main.ScanMode;
import main.Scanner;

public abstract class FunctionLogic {
	private static String identifier;
	private static String returnType;
	private static ArrayList<String> parameterTypes = new ArrayList<String>();
	
	public static int parse(Scanner scanner, String lexeme, String token_class) {
		if (scanner.modeEmpty()) {
			if (scanner.getDeclarationType() == DeclarationType.MainProgram) {
				scanner.print_error(20);
				return 2;
			}
			
			if (lexeme.equals("function")) {
				scanner.pushMode(ScanMode.FunctionDeclare0);
				return 1;
			}
			
			return 0;
		}
		
		switch (scanner.peekMode()) {
		case FunctionDeclare0:
			if (!token_class.equals("identifier")) {
				scanner.print_error(0);
				return 2;
			}
			
			if (scanner.getIdentifier(lexeme) != null) {
				scanner.print_error(5);
				return 2;
			}
			
			identifier = lexeme;
			parameterTypes.clear();
			scanner.setDeclarationType(DeclarationType.ProceduresAndFunctions);
			scanner.popMode();
			scanner.pushMode(ScanMode.FunctionDeclare1);
			return 1;
		case FunctionDeclare1:
			if (!token_class.equals("open parenthesis")) {
				scanner.print_error(10);
				return 2;
			}

			scanner.popMode();
			scanner.pushMode(ScanMode.FunctionVar0);
			return 1;
		case FunctionVar0:
			switch (lexeme) {
			case "const":
				scanner.popMode();
				scanner.pushMode(ScanMode.FunctionVar1);
				return 1;
			case ")":
				scanner.popMode();
				scanner.pushMode(ScanMode.FunctionExit0);
				return 1;
			}
			
			scanner.print_error(11);
			return 2;
		case FunctionVar1:
			if (!token_class.equals("identifier")) {
				scanner.print_error(0);
				return 2;
			}

			scanner.popMode();
			scanner.pushMode(ScanMode.FunctionVar2);
			return 1;
		case FunctionVar2:
			if (!token_class.equals("colon")) {
				scanner.print_error(12);
				return 2;
			}

			scanner.popMode();
			scanner.pushMode(ScanMode.FunctionVar3);
			return 1;
		case FunctionVar3:
			if (!token_class.equals("data type")) {
				scanner.print_error(7);
				return 2;
			}

			parameterTypes.add(lexeme);
			scanner.popMode();
			scanner.pushMode(ScanMode.FunctionVar4);
			return 1;
		case FunctionVar4:
			switch(token_class) {
			case "comma":
				scanner.popMode();
				scanner.pushMode(ScanMode.FunctionVar0);
				return 1;
			case "close parenthesis":
				scanner.popMode();
				scanner.pushMode(ScanMode.FunctionExit0);
				return 1;
			}
			
			return 2;
		case FunctionExit0:
			if (!token_class.equals("colon")) {
				scanner.print_error(12);
				return 2;
			}

			scanner.popMode();
			scanner.pushMode(ScanMode.FunctionExit1);
			return 1;
		case FunctionExit1:
			if (!token_class.equals("data type")) {
				scanner.print_error(7);
				return 2;
			}

			returnType = lexeme;
			scanner.popMode();
			scanner.pushMode(ScanMode.FunctionExit2);
			return 1;
		case FunctionExit2:
			if (!token_class.equals("semicolon")) {
				scanner.print_error(3);
				return 2;
			}

			String[] params = new String[parameterTypes.size()];
			IdentifierFunction target = new IdentifierFunction(
				identifier,
				false,
				false,
				returnType,
				parameterTypes.toArray(params)
			);
			scanner.addIdentifier(target);
			scanner.setTargetProcedure(target);
			scanner.setBodyType(BodyType.Function);
			scanner.popMode();
			scanner.pushMode(ScanMode.FunctionExit3, ScanMode.BodyDeclare0);
			return 1;
		case FunctionExit3:
			if (!token_class.equals("semicolon")) {
				scanner.print_error(3);
				return 2;
			}
			
			scanner.setTargetProcedure(null);
			scanner.setBodyType(BodyType.Main);
			scanner.popMode();
			return 1;
		default:
			return 0;
		}
	}
}
