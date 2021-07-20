public class Scanner {
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
	
	private static String[] arithmetic_operators = new String[] {
		"+",
		"-",
		"/",
		"*"
	};
	
	private static String[] relational_operators = new String[] {
		"=",
		"<>",
		"<",
		">",
		"<=",
		">="
	};
	
	/**
	 * If the scanner is in comment mode.
	 * This allows multiline comment across multiple feed.
	 */
	private String comment = null;
	
	public int read_line(String line) {
		int start = 0;
		
		while (start < line.length()) {
			String lexeme = get_lexeme(start, line);
			start += lexeme.length();
			lexeme = lexeme.trim();
			
			if (lexeme.length() == 0)
				continue;
			
			if (comment != null)
				continue;
			
			String token_class = classify_lexeme(lexeme);
			console_dump(lexeme, token_class);
			parse(lexeme, token_class);
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
	public String get_lexeme(int start, String line) {
		Boolean begin = false;
		Character literal = null;
		int backslash = 0;
		
		for (int i = start; i < line.length(); i++) {
			char c = line.charAt(i);
			
			if (backslash > 0)
				// End backslash mode after 1 character.
				backslash--;
			
			if (!begin && Character.isWhitespace(c))
				// Trim whitespace at the start.
				continue;
			else
				begin = true;
			
			if (c == '\r') {
				// Ignore carriage return.
				continue;
				
			} else if (c == '"' || c == '\'' || c == '`') {
				// Literal mode.
				if (comment != null)
					// In comment mode.
					continue;
				
				if (literal != null) {
					// In literal mode.
					if (literal != c)
						// Does not match terminating character.
						continue;
					
					if (backslash > 0)
						// Used backslash. Don't end literal mode.
						continue;
					
					// Terminate lexeme.
					return line.substring(start, i + 1);
				} else
					// Start literal mode with double quotation mark
					// as terminating character.
					literal = c;
				
			} else if (c == '{') {
				// Comment mode.
				if (literal != null)
					// In literal mode.
					continue;
				
				// Enter comment mode.
				comment = "";
				
			} else if (c == '}') {
				// Attempting to exit comment mode.
				if (comment == null)
					continue;

				if (backslash > 0)
					// Used backslash. Don't end literal mode.
					continue;
				
				// Terminate lexeme.
				String full = comment + line.substring(start, i + 1);
				comment = null;
				return full;
				
			} else if (c == '\\') {
				if (literal != null || comment != null)
					// Toggle backslash mode only during literal mode.
					backslash = 2;
				
			} else if (comment != null) {
				// Currently in comment mode.
				continue;
				
			} else if (c == '\n') {
				// Newline terminates lexeme, except for comments.
				if (literal != null && literal == '}')
					continue;
				
				return line.substring(start, i);
				
			} else if (literal == null) {
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
		
		if (comment != null)
			return comment += line.substring(start) + "\n";
		
		// Return the line if no terminating characters were found.
		return line.substring(start);
	}
	
	public String classify_lexeme(String lexeme) {
		if (contains(reserved, lexeme))
			return "reserved";
		
		if (contains(arithmetic_operators, lexeme))
			return "arithmetic operator";
		
		if (contains(relational_operators, lexeme))
			return "relational operator";
		
		if (lexeme.equals(";"))
			return "semicolon";
		
		if (lexeme.equals(":"))
			return "colon";
		
		if (lexeme.equals("."))
			return "dot";
		
		if (lexeme.equals("("))
			return "open parenthesis";
		
		if (lexeme.equals(")"))
			return "close parenthesis";
		
		if (lexeme.equals(","))
			return "comma";
		
		if (lexeme.equals(":="))
			return "assignment";
		
		if (lexeme.startsWith("{") && lexeme.endsWith("}"))
			return "comment";
		
		if ((lexeme.startsWith("\"") && lexeme.endsWith("\"")) ||
			(lexeme.startsWith("'") && lexeme.endsWith("'")))
			return "literal";
		
		if (lexeme.matches("^-?[0-9]*$"))
			return "integer";
		
		if (lexeme.matches("^[a-zA-Z0-9]*$"))
			return "identifier";
		
		return "unknown";
	}
	
	public int console_dump(String lexeme, String token_class) {
		MainWindow.appendConsoleText(lexeme + "			" + token_class);
		return 0;
	}
	
	/*public int file_dump(File outputFile, String lexeme, String token_class) {
		
	}*/
	
	public void print_error(String code) {
		
	}
	
	public void parse(String lexeme, String token_class) {
		
	}
	
	public static Boolean contains(String[] array, String value) {
		for (String string : array)
			if (string.equals(value))
				return true;
		
		return false;
	}
}
