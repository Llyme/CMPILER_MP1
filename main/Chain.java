package main;

import java.util.ArrayList;

public class Chain {
	private Condition condition;
	private ArrayList<Chain> nexts = new ArrayList<Chain>();
	private boolean failed = false;
	
	private Chain(Condition condition) {
		this.condition = condition;
	}
	
	public static Chain create(Object...items) {
		Chain root = null;
		Chain current = null;
		
		for (Object item : items) {
			if (root == null) {
				if (item instanceof Chain chain)
					root = current = chain;
				else if (item instanceof Condition condition)
					root = current = new Chain(condition);
			} else if (item instanceof Chain chain) {
				current.nexts.add(chain);
				current = chain;
			} else if (item instanceof Condition condition) {
				Chain next = new Chain(condition);
				current.nexts.add(next);
				current = next;
			}
		}
		
		return root;
	}
	
	public static Chain[] or(Chain...chains) {
		return chains;
	}
	
	public boolean condition(String lexeme, String token_class) {
		return condition.fire(lexeme, token_class);
	}
	
	public boolean failed() {
		return failed;
	}
	
	public Chain parse(String lexeme, String token_class) {
		if (nexts.size() == 0) {
			failed = false;
			return null;
		}
		
		for (Chain next : nexts)
			if (next.condition(lexeme, token_class)) {
				failed = false;
				return next;
			}
		
		failed = true;
		return this;
	}

	/**
	 * Get the error code when it causes an error.
	 */
	public int errorCode() {
		Chain[] nexts = new Chain[this.nexts.size()];
		this.nexts.toArray(nexts);
		Condition[] conditions = new Condition[this.nexts.size()];
		
		for (int i = 0; i < conditions.length; i++)
			conditions[i] = nexts[i].condition;
		
		if (!Resources.error_index.containsKey(conditions))
			return -1;
		
		int index = Resources.error_index.get(conditions);
		
		if (index < 0 || index > Resources.error_codes.length - 1)
			return -1;
		
		return index;
	}
	
	public Chain fork(Chain next) {
		nexts.add(next);
		return this;
	}
	
	/**
	 * Add at the end of the chain along index 0.
	 */
	public Chain end(Chain...nexts) {
		Chain curr = this;
		
		while (curr.nexts.size() > 0)
			curr = curr.nexts.get(0);
		
		for (Chain next : nexts)
			curr.nexts.add(next);
		
		return this;
	}
	
	/**
	 * Get last chain along index 0.
	 */
	public Chain tail() {
		Chain curr = this;
		
		while (curr.nexts.size() > 0)
			curr = curr.nexts.get(0);
		
		return curr;
	}
}
