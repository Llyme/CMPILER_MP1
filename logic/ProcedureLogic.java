package logic;

import java.util.ArrayList;

import identifier.*;
import main.*;
import node.*;

public abstract class ProcedureLogic {
	public static final PackageNode declare = new PackageNode();
	public static final PackageNode parameter = new PackageNode();
	public static final PackageNode parameter_append = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"Procedure.Declare",
				Resources.PROCEDURE,
				
				INode.record(),
				
				Resources.IDENTIFIER,
				Resources.OPEN_PARENTHESIS,
				new OrNode(parameter, null),
				Resources.CLOSE_PARENTHESIS,
				Resources.SEMICOLON,
				
				INode.a(() -> interpret()),
				
				BodyLogic.declare,
				Resources.SEMICOLON,
				
				// Pop the intercepting procedure.
				INode.a(() -> Interpreter.procedure(null)),
				
				new OrNode(declare, FunctionLogic.declare, null)
		));
		
		parameter.set(() -> INode.stack(
				"Procedure.Parameter",
				Resources.CONST,
				Resources.IDENTIFIER,
				Resources.COLON,
				Resources.DATA_TYPE,
				new OrNode(parameter_append, null)
		));
		
		parameter_append.set(() -> INode.stack(
				"Procedure.Parameter.Append",
				Resources.COMMA,
				parameter
		));
	}
	
	private static void interpret() {
		LexemeTokenPair[] pairs = Interpreter.flush();
		ArrayList<String> identifiers = new ArrayList<String>();
		ArrayList<String> dataTypes = new ArrayList<String>();
		
		
		// Collect the parameters.
		
		for (int i = 3; i < pairs.length; i += 5) {
			LexemeTokenPair pair = pairs[i];
			
			if (!pair.token().equals("identifier"))
				break;
			
			identifiers.add(pair.lexeme());
			dataTypes.add(pairs[i + 2].lexeme());
		}
		
		
		// Build the procedure.
		
		IdentifierProcedure procedure = new IdentifierProcedure(
				pairs[0].lexeme(),
				false,
				identifiers,
				dataTypes
		);
		
		
		// Add to global and intercept the recorder.
		
		Interpreter.identifiers().addToGlobal(procedure);
		Interpreter.procedure(procedure);
	}
}
