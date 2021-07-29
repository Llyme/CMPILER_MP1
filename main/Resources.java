package main;

public final class Resources {
	public static String OUTPUT_FILENAME = "output.tok";
	public static String ERROR_FILENAME = "error.txt";
	
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
	
	public static String[] error_codes = new String[0];
}
