package node;

import main.Condition;

public class ConditionNode implements INode {
	private Condition condition;
	private String name = "";
	
	public ConditionNode(String name, Condition condition) {
		this.name = name;
		this.condition = condition;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFullName() {
		return "[Condition] " + name;
	}
	
	public boolean parse(String lexeme, String token_class) {
		return condition.fire(lexeme, token_class);
	}
}
