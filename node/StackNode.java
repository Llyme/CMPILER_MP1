package node;

import java.util.Stack;

public class StackNode implements INode {
	public Stack<INode> nodes = new Stack<INode>();
	
	private String name = "";
	
	public StackNode(String name, INode...nodes) {
		this.name = name;
		
		for (int i = nodes.length - 1; i >= 0; i--)
			this.nodes.push(nodes[i]);
	}
	
	public String getName() {
		return name;
	}
	
	public String getFullName() {
		return "[Stack] " + name + " {" + nodes.size() + "}";
	}
}
