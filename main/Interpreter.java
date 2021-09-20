package main;

import java.util.ArrayList;
import java.util.Stack;

import identifier.IdentifierBoolean;
import identifier.IdentifierProcedure;
import identifier.IdentifierType;
import node.ConditionNode;

public abstract class Interpreter {
	// Compilation
	
	private static ArrayList<LexemeTokenPair> pairs =
			new ArrayList<LexemeTokenPair>();
	private static ArrayList<Action> actions =
			new ArrayList<Action>();
	private static IdentifierProcedure procedure = null;
	private static boolean record = false;
	private static String errorMessage = null;
	
	
	// Runtime
	
	private static IdentifierCollection identifiers =
			new IdentifierCollection();
	public static Stack<Stack<Action>> feed =
			new Stack<Stack<Action>>();
	/**
	 * If the interpreter is waiting for a user input.
	 */
	private static boolean read = false;
	public static String input = null;
	
	/**
	 * Record the lexeme-token pair into the array.
	 */
	public static void record(LexemeTokenPair pair) {
		if (!record)
			return;
		
		pairs.add(pair);
	}
	
	public static void record(Action action) {
		if (procedure == null) {
			actions.add(action);
			return;
		}
		
		procedure.append(action);
	}
	
	/**
	 * Start recording any lexeme-token pair read.
	 */
	public static void record() {
		record = true;
	}
	
	public static void record(int offset) {
		if (record)
			return;
		
		record = true;
		
		if (offset < 0)
			offset = -offset;
		
		Scanner scanner = MainWindow.scanner();
		int index = scanner.pairsLength() - 1;
		
		for (; offset > 0; offset--)
			pairs.add(scanner.pair(index - offset));
	}
	
	public static void procedure(IdentifierProcedure procedure) {
		Interpreter.procedure = procedure;
	}
	
	/**
	 * Resets the interpreter's recorder
	 * to its original state.
	 * @return The flushed pairs.
	 */
	public static LexemeTokenPair[] flush() {
		LexemeTokenPair[] pairs =
				new LexemeTokenPair[Interpreter.pairs.size()];
		Interpreter.pairs.toArray(pairs);
		
		Interpreter.pairs.clear();
		record = false;
		return pairs;
	}
	
	/**
	 * Re-creates the identifier collection and
	 * flushes the interpreter.
	 * Use this if you want to start a new interpretation.
	 */
	public static void reset() {
		identifiers = new IdentifierCollection();
		errorMessage = null;
		read = false;
		feed.clear();
		actions.clear();
		flush();
		
		// Initialize predeclared identifiers.
		
		identifiers
		.addToGlobal(new IdentifierType(
				// DATA TYPES
				
				"boolean"
		)).addToGlobal(new IdentifierType(
				"real"
		)).addToGlobal(new IdentifierType(
				"char"
		)).addToGlobal(new IdentifierType(
				"integer"
		)).addToGlobal(new IdentifierType(
				"string"
				
				
				// BOOLEAN
				
		)).addToGlobal(new IdentifierBoolean(
				"true",
				true,
				true // Initial value.
		)).addToGlobal(new IdentifierBoolean(
				"false",
				true,
				false // Initial value.
				
				
				// CALLABLES
				
		)).addToGlobal(new IdentifierProcedure(
				"read",
				true
		)).addToGlobal(new IdentifierProcedure(
				"write",
				true
		)).addToGlobal(new IdentifierProcedure(
				"readln",
				true
		)).addToGlobal(new IdentifierProcedure(
				"writeln",
				true
		));
	}
	
	/**
	 * Returns the identifier collection.
	 */
	public static IdentifierCollection identifiers() {
		return identifiers;
	}
	
	/**
	 * Returns the index of the first pair that satisfies the condition.
	 */
	public static int indexOf
	(LexemeTokenPair[] pairs, Condition condition) {
		for (int i = 0; i < pairs.length; i++) {
			LexemeTokenPair pair = pairs[i];
			
			if (condition.invoke(pair.lexeme(), pair.token()))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Returns the index of the first pair that satisfies the condition.
	 */
	public static int indexOf
	(LexemeTokenPair[] pairs, ConditionNode condition) {
		for (int i = 0; i < pairs.length; i++) {
			LexemeTokenPair pair = pairs[i];
			
			if (condition.parse(pair.lexeme(), pair.token()))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Returns the current error message.
	 */
	public static String error() {
		return errorMessage;
	}
	
	/**
	 * Sets the error message.
	 */
	public static void error(String message) {
		errorMessage = message;
	}
	
	/**
	 * Starts exhausting the interpreter's stacks.
	 */
	public static void begin() {
		feed.push(Helper.arrayToStack(actions));
		resume();
	}
	
	public static void resume() {
		while (!read && feed.size() > 0) {
			Stack<Action> stack = feed.peek();
			
			if (stack.empty()) {
				feed.pop();
				continue;
			}
			
			stack.pop().invoke();
		}
	}
	
	public static void read() {
		read = true;
	}
	
	public static void read(String input) {
		Interpreter.input = input;
		read = false;
	}
}
