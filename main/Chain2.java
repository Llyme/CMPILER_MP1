package main;

import java.util.ArrayList;

public class Chain2 {
	private Condition condition;
	private boolean failed = false;
	
	private Chain2(Condition condition) {
		this.condition = condition;
	}
	
	public static Object[] or(Object...chains) {
		return chains;
	}
}
