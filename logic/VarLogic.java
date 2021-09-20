package logic;

import java.util.ArrayList;

import identifier.*;
import main.*;
import node.*;

public abstract class VarLogic {
	public static final PackageNode declare = new PackageNode();
	
	public static final PackageNode content = new PackageNode();
	public static final PackageNode content_append = new PackageNode();
	
	public static final PackageNode array = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"Var.Declare",
				Resources.VAR,
				content
		));
		
		content.set(() -> INode.stack(
				"Var.Content",
				Resources.IDENTIFIER,
				
				INode.record(1),
				
				new OrNode(content_append, null),
				Resources.COLON,
				new OrNode(array, Resources.DATA_TYPE),
				Resources.SEMICOLON,
				
				INode.a(() -> interpret()),
				
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
				Resources.ARRAY,
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
		
		LexemeTokenPair[] pairs = Interpreter.flush();
		IdentifierCollection collection = Interpreter.identifiers();
		ArrayList<String> identifiers = new ArrayList<String>();
		
		for (LexemeTokenPair pair : pairs) {
			if (!pair.token().equals("identifier"))
				// Not an identifier.
				continue;
			
			if (identifiers.contains(pair.lexeme()) ||
				collection.get(pair.lexeme()) != null) {
				// ERROR! duplicate identifier!
				Interpreter.error("Duplicate identifier token!");
				return;
			}
			
			// Add the lexeme into the array.
			identifiers.add(pair.lexeme());
		}
		
		
		// Get data type.
		
		int index = Interpreter.indexOf(pairs, Resources.COLON);
		String dataType = pairs[index + 1].lexeme();
		
		
		// Generate identifiers.
		
		switch (dataType) {
		case "boolean":
			for (String identifier : identifiers)
				collection.addToGlobal(new IdentifierBoolean(
						identifier,
						false
				));
			break;
		case "character":
			for (String identifier : identifiers)
				collection.addToGlobal(new IdentifierCharacter(
						identifier,
						false
				));
			break;
		case "integer":
			for (String identifier : identifiers) {
				collection.addToGlobal(new IdentifierInteger(
						identifier,
						false
				));
			}
			break;
		case "real":
			for (String identifier : identifiers)
				collection.addToGlobal(new IdentifierReal(
						identifier,
						false
				));
			break;
		case "string":
			for (String identifier : identifiers)
				collection.addToGlobal(new IdentifierString(
						identifier,
						false
				));
			break;
		case "array":
			Func<Identifier> initializer = GenericLogic.identifierInitializer(
					pairs[index + 8].lexeme()
			);
			
			if (initializer == null)
				return;
			
			for (String identifier : identifiers) {
				collection.addToGlobal(new IdentifierArray(
						identifier,
						false,
						dataType,
						Integer.parseInt(pairs[index + 3].lexeme()),
						Integer.parseInt(pairs[index + 5].lexeme()),
						initializer
				));
			}
			break;
		}
	}
}
