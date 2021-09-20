package main;

import java.util.ArrayList;

import node.*;

public class Parser {
	/**
	 * Timelines of nodes.
	 */
	public static INode node = null;
	private static ArrayList<ConditionNode> error_trace =
			new ArrayList<ConditionNode>();
	private static boolean epsilon = false;
	
	public static INode[] getErrorTrace() {
		INode[] result = new INode[error_trace.size()];
		error_trace.toArray(result);
		return result;
	}
	
	/**
	 * Default error message if there is no predefined message.
	 */
	public static String getGenericErrorMessage() {
		if (error_trace.isEmpty())
			return "The parser is no longer accepting inputs (parser stack empty!)";
		
		String result = "Expected ";
		
		if (error_trace.size() == 1)
			return result + "`" + error_trace.get(0).getName() + "`" + "!";
		
		if (error_trace.size() == 2)
			return result +
				"`" + error_trace.get(0).getName() + "` or " +
				"`" + error_trace.get(1).getName() + "`!";
		
		for (int i = 0; i < error_trace.size(); i++) {
			INode node = error_trace.get(i);
			
			if (i != error_trace.size() - 1)
				result += "`" + node.getName() + "`, ";
			else
				result += "or `" + node.getName() + "`!";
		}
		
		return result;
	}
	
	public static Boolean parse(String lexeme, String token_class) {
		if (token_class.equals("comment"))
			// Comments are always ignored, regardless of where it was used.
			return true;
		
		epsilon = false;
		Boolean flag = parse_internal(lexeme, token_class, node, null, 0);
		generateErrorTrace(node);
		
		return flag;
	}
	
	/**
	 * Generates an error trace with the given node,
	 * whereas it will find all ConditionNodes associated.
	 * Takes the first item in a StackNode,
	 * and takes all items in an OrNode.
	 */
	private static void appendErrorTrace(INode node) {
		if (node instanceof StackNode stack) {
			if (!stack.nodes.empty())
				appendErrorTrace(stack.nodes.peek());
			
		} else if (node instanceof OrNode or)
			for (INode node0 : or.nodes)
				appendErrorTrace(node0);
		
		else if (node instanceof ConditionNode condition &&
				!error_trace.contains(condition))
			error_trace.add(condition);
	}
	
	private static void generateErrorTrace(INode node) {
		error_trace.clear();
		
		if (node == null)
			return;
		
		appendErrorTrace(node);
	}
	
	/**
	 * True = success
	 * False = fail
	 */
	private static boolean parse_internal
		(String lexeme,
		String token_class,
		INode node,
		INode parent,
		int index) {
		if (node == null)
			// End-of-file.
			return true;
		
		String trace = node.getFullName();
		
		if (parent != null)
			trace = parent.getFullName() + " > " + trace;
		
		Scanner.log(trace);
		
		if (node instanceof PackageNode pack) {
			// Unpack the stack inside it.
			StackNode stack = pack.unpack();
			
			if (parent != null) {
				// Has a parent. Put the result in it.
				if (parent instanceof StackNode stack0)
					stack0.nodes.push(stack);
				
				else if (parent instanceof OrNode or0)
					or0.nodes[index] = stack;
				
			} else
				// There's no parent, meaning this is the root node.
				Parser.node = stack;
			
			// Parse the stack.
			return parse_internal(
					lexeme,
					token_class,
					stack,
					parent,
					index
			);
		}
		
		if (node instanceof StackNode stack) {
			if (stack.nodes.empty()) {
				// There's nothing inside.
				
				if (parent != null &&
					parent instanceof StackNode stack0 &&
					!stack0.nodes.empty()) {
					// The parent is a stack. Try the next one.
					return parse_internal(
							lexeme,
							token_class,
							stack0.nodes.pop(),
							parent,
							0
					);
				}
				
				// Generate an empty error trace.
				return false;
			}
			
			boolean flag = false;
			
			while (stack.nodes.size() > 0) {
				INode node0 = stack.nodes.pop();
				flag = parse_internal(
						lexeme,
						token_class,
						node0,
						stack,
						0
				);
				
				if (node0 instanceof ActionNode)
					// Ignore ActionNodes.
					continue;
				
				if (flag ||
					!(node0 instanceof StackNode stack0) ||
					stack0.nodes.size() > 0)
					break;
			}
			
			if (parent != null &&
				parent instanceof StackNode stack0) {
				if (!flag || !stack.nodes.empty())
					// Put it back if it failed or it's not empty.
					stack0.nodes.push(stack);
				
				if (epsilon && stack.nodes.empty())
					// Got an epsilon from an OrNode and this is empty.
					// Immediately go to the next node.
					if (!stack0.nodes.empty())
						return parse_internal(
								lexeme,
								token_class,
								node,
								parent,
								0
						);
					else
						// Epsilon on empty stack error.
						return false;
			}
			
			return flag;
		}
		
		if (node instanceof OrNode or) {
			epsilon = false;
			boolean flag = false;
			INode node0 = null;
			
			for (int i = 0; i < or.nodes.length; i++) {
				if (or.nodes[i] == null) {
					epsilon = true;
					continue;
				}
				
				flag = parse_internal(
						lexeme,
						token_class,
						or.nodes[i],
						or,
						i
				);
				
				if (flag) {
					node0 = or.nodes[i];
					break;
				}
			}
			
			if (epsilon) {
				if (parent instanceof StackNode stack0) {
					if (stack0.nodes.empty()) {
						return true;
					} else
						return parse_internal(
								lexeme,
								token_class,
								stack0.nodes.pop(),
								parent,
								0
						);
					
				} else if (parent instanceof OrNode or0) {
					or0.nodes[index] = null;
					return true;
				}
			}
			
			if (flag) {
				if (parent instanceof StackNode stack0) {
					if (!(node0 instanceof ConditionNode))
						// Expose the node.
						// Ignore ConditionNode as it is already consumed.
						stack0.nodes.push(node0);
					
				} else if (parent instanceof OrNode or0)
					// Expose the node.
					or0.nodes[index] = node0;
				
			} else {
				if (parent instanceof StackNode stack0)
					// Put it back.
					stack0.nodes.push(or);
			}
			
			return flag;
		}
		
		if (node instanceof ConditionNode condition)
			if (condition.parse(lexeme, token_class)) {
				epsilon = false;
				return true;
			} else {
				if (parent != null &&
					parent instanceof StackNode stack)
					// Put it back.
					stack.nodes.push(node);
				
				return false;
			}
		
		if (node instanceof ActionNode action)
			// ActionNodes' only purpose is to call a function.
			// It does nothing logical to the parser.
			action.parse(lexeme, token_class);
		
		return false;
	}
}
