import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class Scanner_bak {
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
	
	/**
	 * DISPOSABLE.
	 * DO NOT RE-USE AFTER CALLING THE `run` FUNCTION.
	 */
	public Scanner_bak(String all) {
		this.all = all;
		all_length = all.length();
	}
	
	/**
	 * Returns the current character at the cursor.
	 */
	private Character peek() {
		if (cursor == -1)
			return null;
		
		return all.charAt(cursor);
	}
	
	/**
	 * Moves the cursor to the right.
	 */
	private void next() {
		if (cursor == all_length)
			return;
		
		cursor++;
		
		if (cursor == all_length)
			return;
		
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
		start = 0;
		cursor = 0;
		line = 1;
		
		
		// Program Heading
		
		switch (read_exactly("program")) {
		case 0:
			
		}
		if (read_exactly("program") == -1) {
			// Mandatory keyword consumption.
			print_error("[" + line + "] PASCAL should start with a `program` keyword!");
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
			Boolean flag0 = true; // If variable declaration should continue looping.
			Boolean flag1 = false; // If a data type was declared.
			Boolean flag2 = false; // If at least 1 variable was declared.
			
			// Consumed a keyword for variable declaration. Proceed.
			while (flag0) {
				jump();
				
				switch (read_clean(',', ':', ';', '\n')) {
				case 0:
					switch(peek()) {
					case ',':
						// There's more identifiers. Cache it.
						if (flag1) {
							print_error("[" + line + "] Multiple data type declaration!");
							return;
						}
						
						String word = word().trim(); // Get identifier name.
						
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
						
						if (flag1) {
							print_error("[" + line + "] Multiple data type declaration!");
							return;
						}
						
						flag1 = true; // Data type declared.
						break;
					case ';':
						// Terminating set of variable declaration.
						if (ids.isEmpty()) {
							print_error("[" + line + "] No variables declared!");
							return;
						}
						
						if (!flag1) {
							print_error("[" + line + "] Data type not specified!");
							return;
						}
						
						String dataType = word().trim();
						
						for (String id : ids)
							// Save all identifiers.
							identifiers.add(new Identifier(id, DataType.Integer, false));
						
						flag1 = false; // Reset data type.
						flag2 = true; // At least 1 variable declared. Allow termination.
					case '\n':
						// Terminating variable declaration.
						if (word().trim() != "begin")
							break;
						
						if (!flag2) {
							print_error("[" + line + "] No variable was declared!");
							return;
						}
						
						flag0 = false; // Terminate loop.
						break;
					}
					break;
				case -1:
					print_error("[" + line + "] Invalid identifier character!");
					return;
				case -2:
					print_error("[" + line + "] Token interrupted!");
					return;
				default:
					return;
				}
			}
			
			jump();
		}
		
		jump();
		read_whitespace();
	}
	
	public static int read_line(String line) {
		int start = 0;
		
		while (start < line.length()) {
			String lexeme = get_lexeme(start, line);
			if (lexeme.trim().length() > 0)
				MainWindow.appendConsoleText(lexeme.trim());
			start += lexeme.length();
		}
		//new Scanner(line).run();
		
		return 1;
	}
	
	/**
	 * Returns a lexeme from line.
	 * The last character is the one that terminated the lexeme.
	 * @param start Starting index in the line.
	 * @param line A line of code.
	 * @return Lexeme.
	 */
	public static String get_lexeme(int start, String line) {
		Boolean begin = false;
		Boolean literal = false;
		Boolean backslash = false;
		
		for (int i = start; i < line.length(); i++) {
			char c = line.charAt(i);
			
			if (!begin && Character.isWhitespace(c))
				// Trim whitespace at the start.
				continue;
			else
				begin = true;
			
			if (c == '\r') {
				// Ignore carriage return.
				continue;
				
			} else if (c == '"') {
				if (literal) {
					// In literal mode.
					if (backslash)
						// Used backslash. Don't end literal mode.
						continue;
					else
						// Terminate lexeme.
						return line.substring(start, i + 1);
				} else
					// Start literal mode.
					literal = true;
				
			} else if (c == '\\') {
				if (literal)
					// Toggle backslash mode only during literal mode.
					backslash = !backslash;
				
			} else if (c == '\n') {
				// Newline terminates lexeme.
				return line.substring(start, i);
				
			} else if (!literal) {
				// Past this point is where non-literals are read.
				if (c == ';' ||
					c == ',' ||
					c == '(' ||
					c == ')' ||
					c == '.')
					if (start == i)
						// Just the separator.
						return line.substring(start, i + 1);
					else
						// A lexeme is behind this separator.
						return line.substring(start, i);
				
				if (Character.isWhitespace(c))
					// Don't care about the whitespace. Discard it.
					return line.substring(start, i + 1);
			}
		}
		
		// Return the line if no terminating characters were found.
		return line.substring(start);
	}
	
	public void print_error(String code) {
		
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
	 */
	private int read_clean(char... characters) {
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
