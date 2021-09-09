package main;

import java.util.HashMap;
import node.*;

public final class Resources {
	public static final String OUTPUT_FILENAME = "output.tok";
	public static final String ERROR_FILENAME = "error.txt";
	
	
	public static final String REGEX_REAL = "^-?[0-9]*\\.?[0-9]*$";
	
	
	public static final ConditionNode PROGRAM = new ConditionNode(
			"Program",
			(lexeme, token_class) -> lexeme.equals("program")
		);
	
	public static final ConditionNode IDENTIFIER = new ConditionNode(
			"Identifier",
			(lexeme, token_class) -> token_class.equals("identifier")
		);
	
	public static final ConditionNode PREDECLARED = new ConditionNode(
			"Predeclared",
			(lexeme, token_class) ->
			token_class.equals("predeclared")
		);

	public static final ConditionNode COLON = new ConditionNode(
			"Colon",
			(lexeme, token_class) -> token_class.equals("colon")
		);

	public static final ConditionNode OPEN_BRACKET = new ConditionNode(
			"Open Bracket",
			(lexeme, token_class) -> token_class.equals("open bracket")
		);

	public static final ConditionNode CLOSE_BRACKET = new ConditionNode(
			"Close Bracket",
			(lexeme, token_class) -> token_class.equals("close bracket")
		);

	public static final ConditionNode INTEGER = new ConditionNode(
			"Integer",
			(lexeme, token_class) -> token_class.equals("integer")
		);

	public static final ConditionNode REAL = new ConditionNode(
			"Real",
			(lexeme, token_class) -> token_class.equals("real")
		);

	public static final ConditionNode STRING = new ConditionNode(
			"String",
			(lexeme, token_class) -> token_class.equals("string")
		);

	public static final ConditionNode BOOLEAN = new ConditionNode(
			"Boolean",
			(lexeme, token_class) ->
			lexeme.equals("true") ||
			lexeme.equals("false")
		);

	public static final ConditionNode DOT = new ConditionNode(
			"Dot",
			(lexeme, token_class) -> token_class.equals("dot")
		);

	public static final ConditionNode DOUBLE_DOT = new ConditionNode(
			"Double Dot",
			(lexeme, token_class) -> token_class.equals("double dot")
		);

	public static final ConditionNode OF = new ConditionNode(
			"Of",
			(lexeme, token_class) -> lexeme.equals("of")
		);

	public static final ConditionNode DATA_TYPE = new ConditionNode(
			"Data Type",
			(lexeme, token_class) -> Helper.contains(Resources.data_types, lexeme)
		);

	public static final ConditionNode SEMICOLON = new ConditionNode(
			"Semicolon",
			(lexeme, token_class) -> token_class.equals("semicolon")
		);

	public static final ConditionNode COMMA = new ConditionNode(
			"Comma",
			(lexeme, token_class) -> token_class.equals("comma")
		);

	public static final ConditionNode VAR = new ConditionNode(
			"Var",
			(lexeme, token_class) -> lexeme.equals("var")
		);

	public static final ConditionNode OPEN_PARENTHESIS = new ConditionNode(
			"Open Parenthesis",
			(lexeme, token_class) -> token_class.equals("open parenthesis")
		);

	public static final ConditionNode CLOSE_PARENTHESIS = new ConditionNode(
			"Close Parenthesis",
			(lexeme, token_class) -> token_class.equals("close parenthesis")
		);

	public static final ConditionNode RELATIONAL_OPERATOR = new ConditionNode(
			"Relational Operator",
			(lexeme, token_class) -> token_class.equals("relational operator")
		);

	public static final ConditionNode BOOLEAN_OPERATOR = new ConditionNode(
			"Boolean Operator",
			(lexeme, token_class) -> Helper.contains(Resources.boolean_operators, lexeme)
		);

	public static final ConditionNode BOOLEAN_CONDITIONAL_OPERATOR = new ConditionNode(
			"Boolean Conditional Operator",
			(lexeme, token_class) ->
			lexeme.equals("and") ||
			lexeme.equals("or")
		);
	
	public static final ConditionNode ARITHMETIC_OPERATOR = new ConditionNode(
			"Arithmetic Operator",
			(lexeme, token_class) -> Helper.contains(Resources.arithmetic_operators, lexeme)
		);

	public static final ConditionNode ASSIGNMENT = new ConditionNode(
			"Assignment",
			(lexeme, token_class) -> token_class.equals("assignment")
		);

	public static final ConditionNode NOT = new ConditionNode(
			"Not",
			(lexeme, token_class) -> lexeme.equals("not")
		);

	public static final ConditionNode BEGIN = new ConditionNode(
			"Begin",
			(lexeme, token_class) -> lexeme.equals("begin")
		);

	public static final ConditionNode END = new ConditionNode(
			"End",
			(lexeme, token_class) -> lexeme.equals("end")
		);

	public static final ConditionNode IF = new ConditionNode(
			"If",
			(lexeme, token_class) -> lexeme.equals("if")
		);

	public static final ConditionNode THEN = new ConditionNode(
			"Then",
			(lexeme, token_class) -> lexeme.equals("then")
		);

	public static final ConditionNode ELSE = new ConditionNode(
			"Else",
			(lexeme, token_class) -> lexeme.equals("else")
		);

	public static final ConditionNode FOR = new ConditionNode(
			"For",
			(lexeme, token_class) -> lexeme.equals("for")
		);

	public static final ConditionNode TO = new ConditionNode(
			"To",
			(lexeme, token_class) -> lexeme.equals("to")
		);

	public static final ConditionNode DO = new ConditionNode(
			"Do",
			(lexeme, token_class) -> lexeme.equals("do")
		);

	public static final ConditionNode FUNCTION = new ConditionNode(
			"Function",
			(lexeme, token_class) -> lexeme.equals("function")
		);

	public static final ConditionNode CONST = new ConditionNode(
			"Const",
			(lexeme, token_class) -> lexeme.equals("const")
		);

	public static final ConditionNode PROCEDURE = new ConditionNode(
			"Procedure",
			(lexeme, token_class) -> lexeme.equals("procedure")
		);

	public static final ConditionNode WHILE = new ConditionNode(
			"While",
			(lexeme, token_class) -> lexeme.equals("while")
		);
	
	
	public static final String[] reserved = new String[] {
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
	
	public static final String[] arithmetic_operators = new String[] {
			"+",
			"-",
			"/",
			"*",
			"mod"
	};
	
	public static final String[] relational_operators = new String[] {
			"=",
			"<>",
			"<",
			">",
			"<=",
			">="
	};
	
	public static final String[] boolean_operators = new String[] {
			"and",
			"or",
			"not"
	};
	
	public static final String[] data_types = new String[] {
			"character",
			"string",
			"integer",
			"real",
			"boolean",
			"array"
	};
	
	public static final char[] special_characters = new char[] {
			';',
			',',
			'(',
			')',
			'[',
			']'
	};
	
	public static String[] error_codes = new String[0];
	
	public static HashMap<String[], Integer> error_index =
			new HashMap<String[], Integer>();
}
