package logic;

import identifier.*;
import main.*;

public abstract class VarLogic {
	public static Chain init = Chain.create(
			Resources.VAR
	);
		
	public static Chain prefix = Chain.create(
			Resources.IDENTIFIER,
			Resources.COLON
	);
	
	public static Chain data_type = Chain.create(
			Resources.DATA_TYPE,
			Resources.SEMICOLON
	);
	
	public static Chain array = Chain.create(
			Resources.OPEN_BRACKET,
			Resources.INTEGER,
			Resources.DOUBLE_DOT,
			Resources.INTEGER,
			Resources.CLOSE_BRACKET,
			Resources.OF,
			Resources.DATA_TYPE,
			Resources.SEMICOLON
	);
	
	private static Chain chain = null;
	
	public static void initialize() {
		init.end(
				prefix
		);
		
		prefix.end(
				data_type,
				array
		);
		
		data_type.end(
				prefix
				
		);
		
		array.end(
				prefix
		);
	}
	
	public static int parse(Scanner scanner, String lexeme, String token_class) {
		if (chain != null) {
			chain = chain.parse(lexeme, token_class);
			
			if (chain != null && chain.failed()) {
				scanner.print_error(chain.errorCode());
				return 2;
			}
			
			if (chain != null)
				return 1;
		}
		
		if (lexeme.equals("var")) {
			// var Token
			
			switch (scanner.getDeclarationType()) {
			case Variables:
				scanner.print_error(9);
				return 2;
			case ProceduresAndFunctions:
				scanner.print_error(4);
				return 2;
			default:
				break;
			}
			
			scanner.setDeclarationType(DeclarationType.Variables);
			
			if (scanner.modeEmpty()) {
				scanner.pushMode(ScanMode.VarDeclare0);
				return 1;
			}
			
			switch (scanner.peekMode()) {
			case VarDeclare0:
				scanner.print_error(0);
				return 2;
			default:
				scanner.print_error(2);
				return 2;
			}
		}
		
		if (scanner.modeEmpty())
			return 0;
		
		switch (scanner.peekMode()) {
		case VarDeclare0:
			if (!token_class.equals("identifier")) {
				scanner.print_error(0);
				return 2;
			}
			
			if (scanner.getIdentifier(lexeme) != null) {
				scanner.print_error(5);
				return 2;
			}
			
			scanner.popMode();
			scanner.pushMode(ScanMode.VarDeclare1);
			scanner.addIdentifierQueue(lexeme);
			return 1;
		case VarDeclare1:
			if (token_class.equals("comma")) {
				scanner.popMode();
				scanner.pushMode(ScanMode.VarDeclare0);
				return 1;
			}
			
			if (token_class.equals("colon")) {
				scanner.popMode();
				scanner.pushMode(ScanMode.VarExit0);
				return 1;
			}
			
			scanner.print_error(6);
			return 2;
		case VarExit0:
			if (token_class.equals("open bracket")) {
				chain = array;
				scanner.popMode();
				scanner.pushMode(ScanMode.VarExit2);
				return 1;
			}
			
			if (!Helper.contains(Resources.data_types, lexeme)) {
				scanner.print_error(7);
				return 2;
			}
			
			for (String name : scanner.getIdentifierQueue()) {
				switch(lexeme) {
				case "integer":
					scanner.addIdentifier(new IdentifierInteger(name, false, 0));
					break;
				case "real":
					scanner.addIdentifier(new IdentifierReal(name, false, 0));
					break;
				case "boolean":
					scanner.addIdentifier(new IdentifierBoolean(name, false, false));
					break;
				case "character":
					scanner.addIdentifier(new IdentifierCharacter(name, false, '\u0000'));
				case "string":
					scanner.addIdentifier(new IdentifierString(name, false, null));
				}
			}
			
			scanner.clearIdentifierQueue();
			scanner.popMode();
			scanner.pushMode(ScanMode.VarExit1);
			return 1;
		case VarExit1:
			if (!token_class.equals("semicolon")) {
				scanner.print_error(3);
				return 2;
			}

			scanner.popMode();
			scanner.pushMode(ScanMode.VarExit2);
			return 1;
		case VarExit2:
			switch (lexeme) {
			case "begin":
				scanner.popMode();
				scanner.pushMode(ScanMode.BodyDeclare1);
				return 1;
			case "procedure":
				scanner.popMode();
				scanner.pushMode(ScanMode.ProcedureDeclare0);
				return 1;
			case "function":
				scanner.popMode();
				scanner.pushMode(ScanMode.FunctionDeclare0);
				return 1;
			}
			
			if (token_class.equals("identifier")) {
				if (scanner.getIdentifier(lexeme) != null) {
					scanner.print_error(5);
					return 2;
				}
				
				scanner.popMode();
				scanner.pushMode(ScanMode.VarDeclare1);
				scanner.addIdentifierQueue(lexeme);
				return 1;
			}
			
			scanner.print_error(8);
			return 2;
		default:
			return 0;
		}
	}
}
