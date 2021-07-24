package main;

public enum BodyType {
	/**
	 * If this body is being read from the main program.
	 * This can make changes to any stored data.
	 * This is the default state.
	 */
	Main,
	/**
	 * If this body is being read from the procedure program.
	 * This should not do any changes to any stored data.
	 */
	Procedure,
	/**
	 * If this body is being read from the function program.
	 * This should not do any changes to any stored data.
	 */
	Function
}
