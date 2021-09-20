package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public final class Helper {
	public static Boolean contains(String[] array, String value) {
		for (String item : array)
			if (item.equals(value))
				return true;
		
		return false;
	}
	
	public static Boolean contains(char[] array, char value) {
		for (char item : array)
			if (item == value)
				return true;
		
		return false;
	}
	
	/**
	 * Pushes from index 0.
	 */
	public static <T> Stack<T> arrayToStack(ArrayList<T> array) {
		Stack<T> stack = new Stack<T>();
		
		for (T item : array)
			stack.push(item);
		
		return stack;
	}
	
	/**
	 * Substring equivalent for non-strings.
	 */
	public static LexemeTokenPair[] subsequence
	(LexemeTokenPair[] array, int start, int length) {
		LexemeTokenPair[] result = new LexemeTokenPair[length];
		
		for (int i = 0; i < length; i++)
			result[i] = array[start + i];
		
		return result;
	}
	
	public static Queue<LexemeTokenPair> boolPostfix(LexemeTokenPair[] pairs) {
		return infixToPostfix(
				pairs,
				Resources.boolean_operators0,
				"not",
				pair -> {
					if (pair.token().equals("negation"))
						return 3;
					
					switch (pair.lexeme()) {
					case "=":
					case "<>":
					case "<":
					case "<=":
					case ">=":
						return 1;
					case "and":
					case "or":
						return 2;
					}
					
					return 0;
				}
		);
	}
	
	public static Queue<LexemeTokenPair> arithmeticPostfix(LexemeTokenPair[] pairs) {
		return infixToPostfix(
				pairs,
				Resources.arithmetic_operators,
				"not",
				pair -> {
					if (pair.token().equals("negation"))
						return 3;
					
					switch (pair.lexeme()) {
					case "+":
					case "-":
						return 1;
					case "*":
					case "/":
					case "mod":
						return 2;
					}
					
					return 0;
				}
		);
	}
	
	public static Queue<LexemeTokenPair> infixToPostfix
	(LexemeTokenPair[] pairs,
	String[] operator_lexemes,
	String negation,
	Func1<LexemeTokenPair, Integer> priority) {
		Queue<LexemeTokenPair> result = new LinkedList<LexemeTokenPair>();
		Stack<LexemeTokenPair> operators = new Stack<LexemeTokenPair>();
		/**
		 * If negation is valid.
		 */
		boolean negate = true;
		
		for (LexemeTokenPair pair : pairs) {
			if (pair.lexeme().equals("("))
				operators.push(pair);
			
			else if (pair.lexeme().equals(")")) {
				LexemeTokenPair operator;
				
				while (operators.size() > 0 &&
					!(operator = operators.pop()).lexeme().equals("("))
					result.add(operator);
				
			} else if (!Helper.contains(operator_lexemes, pair.lexeme())) {
				result.add(new LexemeTokenPair(pair.lexeme(), "value"));
				negate = false;
				
				if (!operators.empty() &&
					operators.peek().token().equals("negation"))
					result.add(operators.pop());
			
			} else if (negate && pair.lexeme().equals(negation)) {
				operators.add(new LexemeTokenPair("negation", "operator"));
				negate = false;
				
			} else {
				while (!operators.empty())
				{
					if (priority.invoke(operators.peek()) <= priority.invoke(pair))
						break;

					result.add(operators.pop());
				}

				operators.push(new LexemeTokenPair(pair.lexeme(), "operator"));
				negate = true;
			}
		}
		
		while (!operators.empty())
			if (!operators.peek().lexeme().equals("("))
				result.add(operators.pop());
			else
				return null;
		
		return result;
	}

	/**
	 * This will consume the queue.
	 */
	public static String solveBool(Queue<LexemeTokenPair> queue) {
		LexemeTokenPair pair = solvePostfix(
				queue,
				(a, b, operator) -> {
					boolean value = false;
					
					switch (operator.lexeme()) {
					case "=":
						value = b.lexeme().equals(a.lexeme());
						break;
					case "<>":
						value = !b.lexeme().equals(a.lexeme());
						break;
					case "<":
						value = Float.parseFloat(b.lexeme()) < Float.parseFloat(a.lexeme());
						break;
					case ">":
						value = Float.parseFloat(b.lexeme()) > Float.parseFloat(a.lexeme());
						break;
					case "<=":
						value = Float.parseFloat(b.lexeme()) <= Float.parseFloat(a.lexeme());
						break;
					case ">=":
						value = Float.parseFloat(b.lexeme()) >= Float.parseFloat(a.lexeme());
						break;

					case "and":
						value = Boolean.parseBoolean(b.lexeme()) &&
								Boolean.parseBoolean(a.lexeme());
						break;
					case "or":
						value = Boolean.parseBoolean(b.lexeme()) ||
								Boolean.parseBoolean(a.lexeme());
						break;
					case "negation":
						// `b` has no value with negation.
						value = !Boolean.parseBoolean(a.lexeme());
						break;
					}
					
					return new LexemeTokenPair(Boolean.toString(value), "value");
				}
		);
		
		return pair.lexeme();
	}
	
	/**
	 * This will consume the queue.
	 */
	public static String solveInteger(Queue<LexemeTokenPair> queue) {
		LexemeTokenPair pair = solvePostfix(
				queue,
				(a, b, operator) -> {
					int a0 = Integer.parseInt(a.lexeme());
					int b0 = Integer.parseInt(b.lexeme());
					int value = 0;
					
					switch (operator.lexeme()) {
					case "+":
						value = b0 + a0;
						break;
					case "-":
						value = b0 - a0;
						break;
					case "*":
						value = b0 * a0;
						break;
					case "/":
						value = b0 / a0;
						break;
					case "mod":
						value = b0 % a0;
						break;
					case "negation":
						value = -a0;
						// `b` has no value with negation.
						break;
					}
					
					return new LexemeTokenPair(Integer.toString(value), "value");
				}
		);
		
		return pair.lexeme();
	}

	/**
	 * This will consume the queue.
	 */
	public static String solveFloat(Queue<LexemeTokenPair> queue) {
		LexemeTokenPair pair = solvePostfix(
				queue,
				(a, b, operator) -> {
					float a0 = Float.parseFloat(a.lexeme());
					float b0 = Float.parseFloat(b.lexeme());
					float value = 0;
					
					switch (operator.lexeme()) {
					case "+":
						value = a0 + b0;
						break;
					case "-":
						value = a0 - b0;
						break;
					case "/":
						value = a0 / b0;
						break;
					case "mod":
						value = a0 % b0;
						break;
					case "negation":
						value = -a0;
						// `b` has no value with negation.
						break;
					}
					
					return new LexemeTokenPair(Float.toString(value), "value");
				}
		);
		
		return pair.lexeme();
	}

	/**
	 * This will consume the queue.
	 */
	public static LexemeTokenPair solvePostfix
	(Queue<LexemeTokenPair> queue,
	Func3<LexemeTokenPair, LexemeTokenPair, LexemeTokenPair, LexemeTokenPair> solver) {
		Stack<LexemeTokenPair> values = new Stack<LexemeTokenPair>();
		
		while (!queue.isEmpty()) {
			LexemeTokenPair pair = queue.poll();
			
			switch (pair.token()) {
			case "value":
				values.push(pair);
				break;
			case "operator":
				if (pair.lexeme().equals("negation")) {
					values.push(solver.invoke(values.pop(), null, pair));
					break;
				}
				
				values.push(solver.invoke(values.pop(), values.pop(), pair));
				break;
			}
		}
		
		return values.pop();
	}
}
