package node;

import main.*;

public class ActionNode implements INode {
	private Action action;
	
	public ActionNode(Action action) {
		this.action = action;
	}
	
	public String getName() {
		return "";
	}
	
	public String getFullName() {
		return "[Action]";
	}
	
	public void parse(String lexeme, String token_class) {
		action.invoke();
	}
}
