package logic;

import java.util.ArrayList;

import identifier.IdentifierProcedure;
import main.BodyType;
import main.DeclarationType;
import main.ScanMode;
import main.Scanner;

public abstract class ProcedureLogic {
	private static String identifier;
	private static ArrayList<String> parameterTypes = new ArrayList<String>();
	
	public static int parse(Scanner scanner, String lexeme, String token_class) {
		if (scanner.modeEmpty()) {
			if (scanner.getDeclarationType() == DeclarationType.MainProgram) {
				scanner.print_error(20);
				return 2;
			}
			
			if (lexeme.equals("procedure")) {
				scanner.pushMode(ScanMode.ProcedureDeclare0);
				return 1;
			}
			
			return 0;
		}
		
		switch (scanner.peekMode()) {
		case ProcedureDeclare0:
			// procedure
			if (!token_class.equals("identifier")) {
				scanner.print_error(0);
				return 2;
			}
			
			identifier = lexeme;
			parameterTypes.clear();
			scanner.setDeclarationType(DeclarationType.ProceduresAndFunctions);
			scanner.popMode();
			scanner.pushMode(ScanMode.ProcedureDeclare1);
			return 1;
		case ProcedureDeclare1:
			// procedure Name
			if (!token_class.equals("open parenthesis")) {
				scanner.print_error(10);
				return 2;
			}

			scanner.popMode();
			scanner.pushMode(ScanMode.ProcedureVar0);
			return 1;
		case ProcedureVar0:
			// procedure Name(
			switch (lexeme) {
			case "const":
				scanner.popMode();
				scanner.pushMode(ScanMode.ProcedureVar1);
				return 1;
			case ")":
				scanner.popMode();
				scanner.pushMode(ScanMode.ProcedureExit0);
				return 1;
			}
			
			scanner.print_error(11);
			return 2;
		case ProcedureVar1:
			// procedure Name(const
			if (!token_class.equals("identifier")) {
				scanner.print_error(0);
				return 2;
			}

			scanner.popMode();
			scanner.pushMode(ScanMode.ProcedureVar2);
			return 1;
		case ProcedureVar2:
			// procedure Name(const Name
			if (!token_class.equals("colon")) {
				scanner.print_error(12);
				return 2;
			}

			scanner.popMode();
			scanner.pushMode(ScanMode.ProcedureVar3);
			return 1;
		case ProcedureVar3:
			// procedure Name(const Name :
			if (!token_class.equals("data type")) {
				scanner.print_error(7);
				return 2;
			}

			parameterTypes.add(lexeme);
			scanner.popMode();
			scanner.pushMode(ScanMode.ProcedureVar4);
			return 1;
		case ProcedureVar4:
			// procedure Name(const Name : DataType
			switch(lexeme) {
			case ",":
				scanner.popMode();
				scanner.pushMode(ScanMode.ProcedureVar0);
				return 1;
			case ")":
				scanner.popMode();
				scanner.pushMode(ScanMode.ProcedureExit0);
				return 1;
			}
			
			scanner.print_error(21);
			return 2;
		case ProcedureExit0:
			if (!token_class.equals("semicolon")) {
				scanner.print_error(3);
				return 2;
			}
			
			String[] params = new String[parameterTypes.size()];
			IdentifierProcedure target = new IdentifierProcedure(
				identifier,
				false,
				false,
				parameterTypes.toArray(params)
			);
			scanner.addIdentifier(target);
			scanner.setTargetProcedure(target);
			scanner.setBodyType(BodyType.Procedure);
			scanner.popMode();
			scanner.pushMode(ScanMode.ProcedureExit1, ScanMode.BodyDeclare0);
			return 1;
		case ProcedureExit1:
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
