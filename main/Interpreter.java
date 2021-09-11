package main;

import java.util.ArrayList;

import node.ConditionNode;

public abstract class Interpreter {
	private static IdentifierCollection identifiers =
			new IdentifierCollection();
	
	private static ArrayList<LexemeTokenPair> array =
			new ArrayList<LexemeTokenPair>();
	private static boolean record = false;
	
	/**
	 * Record the lexeme-token pair into the array.
	 */
	public static void record(LexemeTokenPair pair) {
		if (!record)
			return;
		
		array.add(pair);
	}
	
	/**
	 * Start recording any lexeme-token pair read.
	 */
	public static void record() {
		record = true;
	}
	
	/**
	 * Resets the interpreter's recorder
	 * to its original state.
	 */
	public static void flush() {
		array.clear();
		record = false;
	}
	
	/**
	 * Re-creates the identifier collection and
	 * flushes the interpreter.
	 * Use this if you want to start a new interpretation.
	 */
	public static void reset() {
		identifiers = new IdentifierCollection();
		flush();
	}
	
	/**
	 * Returns the identifier collection.
	 */
	public static IdentifierCollection identifiers() {
		return identifiers;
	}
	
	/**
	 * Returns the lexeme-token pair at the given index.
	 * Returns an empty pair if it doesn't exist.
	 */
	public static LexemeTokenPair get(int index) {
		if (index < 0 || index > array.size() - 1)
			return LexemeTokenPair.EMPTY;
		
		return array.get(index);
	}
	
	/**
	 * Returns the number of pairs in the array.
	 */
	public static int size() {
		return array.size();
	}
	
	/**
	 * Returns the index of the first pair that satisfies the condition.
	 */
	public static int indexOf(Condition condition) {
		for (int i = 0; i < array.size(); i++) {
			LexemeTokenPair pair = array.get(i);
			
			if (condition.fire(pair.lexeme(), pair.token()))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Returns the index of the first pair that satisfies the condition.
	 */
	public static int indexOf(ConditionNode condition) {
		for (int i = 0; i < array.size(); i++) {
			LexemeTokenPair pair = array.get(i);
			
			if (condition.parse(pair.lexeme(), pair.token()))
				return i;
		}
		
		return -1;
	}
}
