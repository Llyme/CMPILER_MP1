package node;

import main.*;

public interface INode {
	public String getName();
	public String getFullName();
	
	public static StackNode stack(String name, INode...nodes) {
		return new StackNode(name, nodes);
	}
	
	public static StackNode stack(INode...nodes) {
		return new StackNode("", nodes);
	}
	
	public static INode[] array(INode...nodes) {
		return nodes;
	}
	
	public static ActionNode a(Action action) {
		return new ActionNode(action);
	}
	
	public static ActionNode record() {
		return new ActionNode(() -> Interpreter.record());
	}
	
	public static ActionNode record(int offset) {
		return new ActionNode(() -> Interpreter.record(offset));
	}
}
