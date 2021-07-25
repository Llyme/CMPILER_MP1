package main;

public final class Resources {
	public static String[] reserved = new String[] {
			"program",
			"and",
			"array",
			"for",
			"while",
			"begin",
			"or",
			"of",
			"to",
			"do",
			"end",
			"not",
			"if",
			"downto",
			"mod",
			"function",
			"var",
			"then",
			"repeat",
			"div",
			"procedure",
			"const",
			"else",
			"until",
			"return"
	};
	
	public static String[] arithmetic_operators = new String[] {
		"+",
		"-",
		"/",
		"*",
		"mod"
	};
	
	public static String[] relational_operators = new String[] {
		"=",
		"<>",
		"<",
		">",
		"<=",
		">="
	};
	
	public static String[] boolean_operators = new String[] {
		"and",
		"or",
		"not"
	};
	
	public static String[] data_types = new String[] {
		"character",
		"string",
		"integer",
		"real",
		"boolean",
		"array"
	};
	
	public static String[] error_codes = new String[] {
		"Expected identifier!", // 0
		"Program name must be declared first!", // 1
		"Parse order tampered!", // 2
		"Expected semicolon!", // 3
		"Variable declarations must be done before procedure/function declarations!", // 4
		"Identifier token already used!", // 5
		"Expected separator!", // 6
		"Expected data type!", // 7
		"Expected identifier, procedure, or function!", // 8
		"Only 1 `var` token is needed!", // 9
		"Expected open parenthesis!", // 10
		"Expected `const` token or close parenthesis!", // 11
		"Expected colon!", // 12
		"Expected `begin` token!", // 13
		"Invalid token!", // 14
		"Identifier does not exist!", // 15
		"Identifier data type mismatch!", // 16
		"Data type cannot perform arithmetic!", // 17
		"Expected arithmetic operator!", // 18
		"Expected dot!", // 19
		"Procedure and function declaration should be done before the main program!", // 20
		"Expected comma or close parenthesis!", // 21
		"Expected close parenthesis!", // 22
		"Identifier is not callable!", // 23
		"Procedure/function parameter count mismatch!", // 24
		"Expected assignment token!", // 25
		"Invalid syntax for this data type!" // 26
	};
}
