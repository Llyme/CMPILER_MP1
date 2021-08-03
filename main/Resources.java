package main;

import java.util.HashMap;

public final class Resources {
	public static final Condition IDENTIFIER =
			(lexeme, token_class) -> token_class.equals("identifier");

	public static final Condition COLON =
			(lexeme, token_class) -> token_class.equals("colon");

	public static final Condition OPEN_BRACKET =
			(lexeme, token_class) -> token_class.equals("open bracket");

	public static final Condition CLOSE_BRACKET =
			(lexeme, token_class) -> token_class.equals("close bracket");

	public static final Condition INTEGER =
			(lexeme, token_class) -> token_class.equals("integer");

	public static final Condition DOUBLE_DOT =
			(lexeme, token_class) -> token_class.equals("double dot");

	public static final Condition OF =
			(lexeme, token_class) -> lexeme.equals("of");

	public static final Condition DATA_TYPE =
			(lexeme, token_class) -> Helper.contains(Resources.data_types, lexeme);

	public static final Condition SEMICOLON =
			(lexeme, token_class) -> token_class.equals("semicolon");

	public static final Condition COMMA =
			(lexeme, token_class) -> token_class.equals("comma");

	public static final Condition VAR =
			(lexeme, token_class) -> token_class.equals("var");

	public static final Condition OPEN_PARENTHESIS =
			(lexeme, token_class) -> token_class.equals("open parenthesis");

	public static final Condition CLOSE_PARENTHESIS =
			(lexeme, token_class) -> token_class.equals("close parenthesis");
	
	public static final Condition RELATIONAL_VALUE =
			(lexeme, token_class) ->
			token_class.equals("integer") ||
			token_class.equals("real") ||
			token_class.equals("identifier") ||
			lexeme.equals("true") ||
			lexeme.equals("false");

	public static final Condition RELATIONAL_OPERATOR =
			(lexeme, token_class) -> token_class.equals("relational operator");

	public static final Condition BOOLEAN_OPERATOR =
			(lexeme, token_class) -> Helper.contains(Resources.boolean_operators, lexeme);

	public static final Condition ASSIGNMENT =
			(lexeme, token_class) -> token_class.equals("assignment");

	public static final Condition NOT =
			(lexeme, token_class) -> lexeme.equals("not");
			
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
	
	public static HashMap<Condition[], Integer> error_index =
			new HashMap<Condition[], Integer>();
}
