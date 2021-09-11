package logic;

import java.util.ArrayList;

import main.*;
import node.*;

public abstract class VarLogic {
	public static final PackageNode declare = new PackageNode();
	
	public static final PackageNode content = new PackageNode();
	public static final PackageNode content_append = new PackageNode();
	
	public static final PackageNode array = new PackageNode();
	
	private static final ConditionNode SEMICOLON_AND_INTERPRET =
			new ConditionNode(
				"Semicolon",
				(lexeme, token_class) -> {
					boolean flag = Resources.SEMICOLON.parse(
							lexeme,
							token_class
					);
					
					if (flag) {
						interpret();
						Interpreter.flush();
					}
					
					return flag;
				}
			);
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"Var.Declare",
				Resources.VAR,
				content
		));
		
		content.set(() -> INode.stack(
				"Var.Content",
				Resources.IDENTIFIER_AND_RECORD,
				new OrNode(content_append, null),
				Resources.COLON,
				new OrNode(array, Resources.DATA_TYPE),
				SEMICOLON_AND_INTERPRET,
				new OrNode(content, null)
		));
		
		content_append.set(() -> INode.stack(
				"Var.Content.Append",
				Resources.COMMA,
				Resources.IDENTIFIER,
				new OrNode(content_append, null)
		));
		
		array.set(() -> INode.stack(
				"Var.Array",
				Resources.OPEN_BRACKET,
				Resources.INTEGER,
				Resources.DOUBLE_DOT,
				Resources.INTEGER,
				Resources.CLOSE_BRACKET,
				Resources.OF,
				Resources.DATA_TYPE
		));
	}
	
	public static void interpret() {
		// Get all of the identifiers.
		
		ArrayList<String> identifiers = new ArrayList<String>();
		
		for (int i = 0; i < Interpreter.size(); i++) {
			LexemeTokenPair pair = Interpreter.get(i);
			
			if (!pair.token().equals("identifier"))
				// Not an identifier.
				continue;
			
			if (identifiers.contains(pair.lexeme())) {
				// ERROR! duplicate identifier!
				return;
			}
			
			// Add the lexeme into the array.
			identifiers.add(pair.lexeme());
		}
		
		
		// Get data type.
		
		int index = Interpreter.indexOf(Resources.DATA_TYPE);
		
		if (index == -1) {
			// FATAL ERROR! No data type!
			// This should not be possible as the parser
			// filters this out.
			return;
		}
		
		String datatype = Interpreter.get(index).lexeme();
		
		
		//
		
		for (var q : identifiers)
			MainWindow.appendConsoleText("WROTE " + q + " as " + datatype);
	}
}
