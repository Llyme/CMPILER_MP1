package node;

import main.Predicate;

public class PackageNode implements INode {
	private Predicate predicate;
	private String name = "";
	
	public PackageNode() {
	}
	
	public PackageNode(String name) {
		this.name = name;
	}
	
	public PackageNode set(Predicate predicate) {
		if (this.predicate != null)
			return this;
		
		this.predicate = predicate;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFullName() {
		return "[Package] " + name;
	}
	
	public StackNode unpack() {
		return predicate.fire();
	}
}
