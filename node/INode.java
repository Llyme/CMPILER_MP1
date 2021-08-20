package node;

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
}
