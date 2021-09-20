package main;

import java.util.HashMap;
import java.util.Stack;

import identifier.Identifier;

/**
 * A collection of identifiers.
 * To reset, just create a new one.
 */
public class IdentifierCollection {
	/**
	 * Global identifiers.
	 */
	public final HashMap<String, Identifier> global =
			new HashMap<String, Identifier>();
	/**
	 * Temporary identifiers.
	 */
	private final Stack<HashMap<String, Identifier>> scopes =
			new Stack<HashMap<String, Identifier>>();
	
	/**
	 * Checks in the sub-scope first,
	 * then checks the global identifiers.
	 * Returns `null` if it doesn't exist.
	 */
	public Identifier get(String name) {
		if (scopes.size() > 0) {
			HashMap<String, Identifier> scope = scopes.peek();
			
			if (scope.containsKey(name))
				// Has an identifier in the current scope
				// overriding global identifiers.
				return scope.get(name);
		}
		
		if (global.containsKey(name))
			return global.get(name);
		
		return null;
	}
	
	/**
	 * Adds a new identifier to the global identifiers.
	 */
	public IdentifierCollection addToGlobal(Identifier identifier) {
		String name = identifier.getName();
		
		if (global.containsKey(name))
			// Error! This key is already occupied!
			return this;
		
		global.put(name, identifier);
		
		return this;
	}
	
	/**
	 * Adds a new identifier to the current scope.
	 */
	public IdentifierCollection addToScope(Identifier identifier) {
		if (scopes.size() == 0)
			// Error! Not in a scope!
			return this;
		
		HashMap<String, Identifier> scope = scopes.peek();
		String name = identifier.getName();
		
		if (scopes.peek().containsKey(name))
			// Error! This key is already occupied!
			return this;
		
		scope.put(name, identifier);
		return this;
	}
	
	/**
	 * Pushes a new scope.
	 */
	public void pushScope() {
		scopes.push(new HashMap<String, Identifier>());
	}
	
	/**
	 * Pops the last scope added.
	 */
	public void popScope() {
		scopes.pop();
	}
}
