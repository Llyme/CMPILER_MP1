import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class Scanner {
	private interface IteratorPredicate {
		int run(int step, char c);
	}
	
	private interface CharacterPredicate {
		Boolean run(char c);
	}
	
	private static String[] reserved = new String[] {
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
	/*
	 * These separators are ignored if used inside a literal.
	 */
	private static char[] separators = new char[] {
		';'
	};
	/*
	 * Same as regular separators.
	 */
	private static char[] separators_whitespace = new char[] {
		' ',
		'	',
		'\n'
	};
	/*
	 * Separators that will be respected if being used inside a literal,
	 * unless a backslash is used.
	 */
	private static char[] separators_literal = new char[] {
		'\n'
	};
	
	private static char[][] comments = new char[][] {
		new char[] { '{', '}' }
	};
	
	private static char[] literals = new char[] {
		'"',
		'\'',
		'`'
	};
	
	private static ArrayList<Identifier> identifiers = new ArrayList<Identifier>() {{
		new Identifier("boolean", DataType.Boolean, true);
		new Identifier("real", DataType.Real, true);
		new Identifier("true", DataType.Boolean, true);
		new Identifier("read", DataType.String, true);
		new Identifier("write", DataType.String, true);
		new Identifier("char", DataType.Character, true);
		new Identifier("integer", DataType.Integer, true);
		new Identifier("false", DataType.Boolean, true);
		new Identifier("readln", DataType.String, true);
		new Identifier("writeln", DataType.String, true);
		new Identifier("string", DataType.String, true);
	}};
	
	// Scanner configuration. DO NOT EDIT
	private int line = 0;
	private int start = 0;
	private int cursor = 0;
	private int all_length = 0;
	private String all = null;
	private Boolean done = false;
	
	/**
	 * DISPOSABLE.
	 * DO NOT RE-USE AFTER CALLING THE `run` FUNCTION.
	 */
	public Scanner(String all) {
		this.all = all;
		all_length = all.length();
	}
	
	/**
	 * Returns the current character at the cursor.
	 */
	private Character peek() {
		if (done)
			return null;
		
		if (cursor == -1)
			return null;
		
		return all.charAt(cursor);
	}
	
	/**
	 * Moves the cursor to the right.
	 */
	private void next() {
		if (done)
			return;
		
		if (cursor == all_length)
			return;
		
		cursor++;
		
		if (cursor == all_length) {
			done = true;
			return;
		}
		
		char c = peek();
		
		if (c == '\n')
			line++;
	}
	
	/**
	 * Move start to cursor.
	 */
	private void jump() {
		start = cursor;
	}
	
	/**
	 * Form a word from start to cursor,
	 * excluding the last character.
	 */
	private String word() {
		return all.substring(start, cursor - start - 1);
	}
	
	/**
	 * Run's the scanner.
	 * This object should be disposed after calling this function.
	 */
	
	public void run() {
		if (done)
			// Disposable objects should only be used once.
			return;
		
		start = 0;
		cursor = 0;
		line = 1;
		
		
		// Program Heading
		
		if (read_exactly("program") == -1) {
			// Mandatory keyword consumption.
			print_error("[" + line + "] PASCAL should start with a `program` keyword!");
			done = true;
			return;
		}
		
		jump();
		read_whitespace();
		jump();
		
		if (read_clean(';') == -1) {
			print_error("[" + line + "] Invalid identifier token!");
			return;
		}
		
		String programName = word().trim(); // Get program name.
		jump();
		
		
		// Variable Declaration

		read_whitespace();
		
		if (read_exactly("var") == 0) {
			ArrayList<String> ids = new ArrayList<String>();
			Boolean flag = false;
			
			// Consumed a keyword for variable declaration. Proceed.
			while (true) {
				jump();
				
				switch (read_clean(',', ':', ';')) {
				case 0:
					switch(peek()) {
					case ',':
						// There's more identifiers. Cache it.
						if (flag) {
							print_error("[" + line + "] Multiple data type declaration!");
							return;
						}
						
						String word = word().trim();
						
						if (ids.contains(word) ||
							has_identifier(word)) {
							print_error("[" + line + "] Multiple variable token!");
							return;
						}
						
						break;
					case ':':
						// Data type is being declared.
						if (ids.isEmpty()) {
							print_error("[" + line + "] No variables declared!");
							return;
						}
						
						if (flag) {
							print_error("[" + line + "] Multiple data type declaration!");
							return;
						}
						
						flag = true;
						break;
					case ';':
						// Terminating variable declaration.
						if (ids.isEmpty()) {
							print_error("[" + line + "] No variables declared!");
							return;
						}
						
						if (!flag) {
							print_error("[" + line + "] Data type not specified!");
							return;
						}
						
						String dataType = word().trim();
						
						for (String id : ids)
							identifiers.add(new Identifier(id, DataType.Integer, false));
						flag = false;
					}
					continue;
				case -1:
					print_error("[" + line + "] Invalid identifier character!");
					return;
				case -2:
					print_error("[" + line + "] Token interrupted!");
					return;
				default:
					return;
				}
				
				break;
			}
			
			jump();
		}
		
		jump();
		read_whitespace();
	}
	
	private static Boolean is_reserved(String text) {
		for (int i = 0; i < reserved.length; i++)
			if (text == reserved[i])
				return true;
		
		return false;
	}
	
	// Returns a literal token if it exists.
	private static Character get_literal(char c) {
		for (int i = 0; i < literals.length; i++)
			if (c == literals[i])
				return literals[i];
		
		return null;
	}
	
	// If this is a respected separator.
	private static Boolean is_separator(char c, Boolean literal) {
		if (!literal) {
			for (int i = 0; i < separators.length; i++)
				if (c == separators[i])
					return true;
			
			for (int i = 0; i < separators_whitespace.length; i++)
				if (c == separators_whitespace[i])
					return true;
		}

		for (int i = 0; i < separators_literal.length; i++)
			if (c == separators_literal[i])
				return true;
		
		return false;
	}
	
	public static int read_line(File inputFile, String line) {
		String result = "";
		int n = 0;
		Character literal = null; // Last used literal token.
		
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			
			
			if (is_separator(c, literal != null)) {
				if (n != i) {
					get_lexeme(line.substring(n, i));
				}
				
				n = i;
			}
		}
		
		MainWindow.setConsoleText(line);
		
		return 1;
	}
	
	/*public static String get_lexeme(String line) {
	}
	
	public static String classify_lexeme(String lexeme) {
		
	}
	
	public static int console_dump(String lexeme, String token_class) {
	}
	
	public static int file_dump(File outputFile, String lexeme, String token_class) {
		
	}*/
	
	public static void print_error(String code) {
		
	}
	
	
	public static void ScanContinuous(String text) {
	}
	
	public static void ScanPerLine(String text) {
	}
	
	/*
	 * The next character must match the given lexeme.
	 * Returns 0 if no errors, otherwise -1.
	 */
	private int read_exactly(String lexeme) {
		if (done)
			return -1;
		
		if (all_length - cursor < lexeme.length())
			// Not enough characters.
			return -1;
		
		while (cursor < all_length) {
			if (peek() != lexeme.charAt(cursor))
				return -1;
			
			next();
		}
		
		return 0;
	}
	
	/**
	 * Consume whitespace.
	 */
	private void read_whitespace() {
		if (done)
			return;
		
		while (cursor < all_length) {
			if (!Character.isWhitespace(peek()))
				return;
			
			next();
		}
	}
	
	/**
	 * Read until predicate returns `true`.
	 */
	private void read_until(CharacterPredicate predicate) {
		if (done)
			return;
		
		while (cursor < all_length) {
			if (predicate.run(peek()))
				return;
			
			next();
		}
	}
	
	/**
	 * Read until one of the given set of characters is read.
	 * Throws an error if a whitespace is read
	 * between 2 non-whitespace characters.
	 * -1 = whitespace error.
	 * -2 = did not end properly.
	 * -3 = scanner is dead.
	 */
	private int read_clean(char... characters) {
		if (done)
			return -3;
		
		int flag = 0;
		
		while (cursor < all_length) {
			if (Character.isWhitespace(peek())) {
				if (flag == 1)
					// Read a whitespace after a non-whitespace.
					flag = 2;
			} else if (flag == 0)
				// Started with a non-whitespace.
				flag = 1;
			else if (flag == 2)
				// A non-whitespace is read after a whitespace.
				return -1;
			
			for (char c : characters)
				if (peek() == c)
					// Ended properly.
					return 0;
			
			next();
		}
		
		return characters.length > 0
			// Did not end properly.
			? -2
			// Ended properly since there's no end character.
			: 0;
	}
	
	private static Boolean has_identifier(String name) {
		for (Identifier identifier : identifiers)
			if (identifier.getName() == name)
				return true;
		
		return false;
	}
}
