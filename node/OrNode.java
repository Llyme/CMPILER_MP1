package node;

public class OrNode implements INode {
	public INode[] nodes;
	
	private String name = "";
	
	public OrNode(INode...nodes) {
		this.nodes = nodes;
	}
	
	public OrNode(String name, INode...nodes) {
		this.name = name;
		this.nodes = nodes;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFullName() {
		return "[Or] " + name + " {" + nodes.length + "}";
	}
}
