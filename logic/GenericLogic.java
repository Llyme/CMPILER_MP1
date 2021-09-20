package logic;

import java.util.ArrayList;

import identifier.Identifier;
import identifier.IdentifierBoolean;
import identifier.IdentifierCharacter;
import identifier.IdentifierInteger;
import identifier.IdentifierProcedure;
import identifier.IdentifierReal;
import identifier.IdentifierString;
import main.*;
import node.*;

public abstract class GenericLogic {
	public static final PackageNode IDENTIFIER_OR_CALLABLE = new PackageNode();
	public static final PackageNode NUMBER = new PackageNode();
	
	public static void initialize() {
		IDENTIFIER_OR_CALLABLE.set(() -> INode.stack(
				"IDENTIFIER_OR_CALLABLE",
				Resources.IDENTIFIER,
				new OrNode(
						INode.stack(
								Resources.OPEN_PARENTHESIS,
								new OrNode(
										AssignmentLogic.content,
										AssignmentLogic.group
								),
								Resources.CLOSE_PARENTHESIS
						),
						null
				)
		));
		NUMBER.set(() -> INode.stack(
				"Number",
				new OrNode(
						Resources.INTEGER,
					Resources.REAL
				)
		));
	}
	
	public static Identifier getIdentifier(String name) {
		Identifier identifier = Interpreter.identifiers().get(name);
		
		if (identifier == null)
			Interpreter.error("Identifier `" + name + "` does not exist!");
		
		return identifier;
	}
	
	public static Func<Identifier> identifierInitializer(String dataType) {
		switch (dataType) {
		case "boolean":
			return () -> new IdentifierBoolean(null, false);
		case "character":
			return () -> new IdentifierCharacter(null, false);
		case "integer":
			return () -> new IdentifierInteger(null, false);
		case "real":
			return () -> new IdentifierReal(null, false);
		case "string":
			return () -> new IdentifierString(null, false);
		}
		
		Interpreter.error("Invalid data type!");
		return null;
	}
	
	public static ArrayList<LexemeTokenPair[]> getParameters
	(LexemeTokenPair[] pairs, int start, int end) {
		ArrayList<LexemeTokenPair[]> parameters =
				new ArrayList<LexemeTokenPair[]>();
		ArrayList<LexemeTokenPair> parameter =
				new ArrayList<LexemeTokenPair>();
		
		for (int i = start; i < end; i++) {
			LexemeTokenPair pair = pairs[i];
			
			if (pair.lexeme().equals(",")) {
				LexemeTokenPair[] parameter0 =
						new LexemeTokenPair[parameter.size()];
				parameter.toArray(parameter0);
				parameters.add(parameter0);
				parameter = new ArrayList<LexemeTokenPair>();
				continue;
			}
			
			parameter.add(pair);
		}
		
		if (parameter.size() > 0) {
			LexemeTokenPair[] parameter0 =
					new LexemeTokenPair[parameter.size()];
			parameter.toArray(parameter0);
			parameters.add(parameter0);
		}
		
		return parameters;
	}
	
	public static boolean checkParameters
	(IdentifierProcedure procedure, ArrayList<LexemeTokenPair[]> parameters) {
		if (parameters.size() != procedure.parameterLength()) {
			Interpreter.error(
					"Wrong number of parameters specified for call to `" +
					procedure.getName() +
					"`!"
			);
			return false;
		}
		
		return true;
	}
}
