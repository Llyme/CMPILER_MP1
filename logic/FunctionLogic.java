package logic;

import java.util.ArrayList;

import identifier.Identifier;
import identifier.IdentifierFunction;
import identifier.IdentifierInteger;
import main.Func;
import main.Interpreter;
import main.LexemeTokenPair;
import main.Resources;
import node.*;

public abstract class FunctionLogic {
	public static final PackageNode declare = new PackageNode();
	public static final PackageNode parameter = new PackageNode();
	public static final PackageNode parameter_append = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"Function.Declare",
				Resources.FUNCTION,
				
				INode.record(),
				
				Resources.IDENTIFIER,
				Resources.OPEN_PARENTHESIS,
				new OrNode(parameter, null),
				Resources.CLOSE_PARENTHESIS,
				Resources.COLON,
				Resources.DATA_TYPE,
				Resources.SEMICOLON,
				
				INode.a(() -> interpret()),
				
				BodyLogic.declare,
				Resources.SEMICOLON,
				
				// Pop the intercepting function.
				INode.a(() -> Interpreter.procedure(null)),
				
				new OrNode(declare, ProcedureLogic.declare, null)
		));
		
		parameter.set(() -> INode.stack(
				"Function.Parameter",
				Resources.CONST,
				Resources.IDENTIFIER,
				Resources.COLON,
				Resources.DATA_TYPE,
				new OrNode(parameter_append, null)
		));
		
		parameter_append.set(() -> INode.stack(
				"Function.Parameter.Append",
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
		
		
		// Build the function.
		
		Func<Identifier> returnValueInitializer = GenericLogic
				.identifierInitializer(pairs[pairs.length - 2].lexeme());
		
		if (returnValueInitializer == null)
			return;
		
		IdentifierFunction function = new IdentifierFunction(
				pairs[0].lexeme(),
				false,
				returnValueInitializer,
				identifiers,
				dataTypes
		);
		
		
		// Add to global and intercept the recorder.
		
		Interpreter.identifiers().addToGlobal(function);
		Interpreter.procedure(function);
	}
}
