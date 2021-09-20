package main;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import identifier.*;
import logic.*;
import node.*;

public class Scanner {
	private ArrayList<LexemeTokenPair> pairs =
			new ArrayList<LexemeTokenPair>();
	/**
	 * If the scanner is in comment mode.
	 * This allows multiline comment across multiple feed.
	 */
	private String comment = null;
	private String currentLine = null;
	
	public Scanner() {
		// Initialize predefined identifiers.
		Interpreter.reset();
		Parser.node = RootLogic.declare;
	}
	
	public String getCurrentLine() {
		return currentLine;
	}
	
	public LexemeTokenPair pair(int index) {
		return pairs.get(index);
	}
	
	public int pairsLength() {
		return pairs.size();
	}
	
	public int read_line(String line) {
		int start = 0;
		currentLine = line;
		
		while (start < line.length()) {
			String lexeme = get_lexeme(start, line);
			start += lexeme.length();
			lexeme = lexeme.trim();
			
			if (lexeme.length() == 0)
				continue;
			
			if (comment != null)
				continue;
			
			String token_class = classify_lexeme(lexeme);
			pairs.add(new LexemeTokenPair(lexeme, token_class));
			console_dump(lexeme, token_class);
			file_dump(lexeme, token_class);
			boolean flag = Parser.parse(lexeme, token_class);
			log("[Result] " + flag);
			log("");
			
			if (flag) {
				// Only interpret if there are no errors.
				Interpreter.record(new LexemeTokenPair(lexeme, token_class));
				String error = Interpreter.error();
				
				if (error != null) {
					// An error occured. Forcibly end the loop.
					print_error(error);
					break;
				}
				
			} else {
				// Trace the error and print it.
				INode[] error_trace = Parser.getErrorTrace();
				String[] flag0 = null;
				
				for (String[] keys : Resources.error_index.keySet()) {
					if (error_trace.length != keys.length)
						continue;
					
					for (int i = 0; i < keys.length; i++)
						if (error_trace[i].getName().equals(keys[i])) {
							flag0 = keys;
							break;
						}
					
					if (flag0 != null)
						break;
				}
				
				if (flag0 != null)
					print_error(Resources.error_index.get(flag0));
				else
					print_error(Parser.getGenericErrorMessage());
			}
		}
		
		
		// Execute the action sequence.
		
		Interpreter.begin();
		
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
		/**
		 * 0 = Not began;
		 * 1 = Just Began;
		 * 2 = Began;
		 */
		int began = 0;
		Character literal = null;
		int backslash = 0;
		boolean dot = false;
		
		for (int i = start; i < line.length(); i++) {
			char c = line.charAt(i);
			
			if (backslash > 0)
				// End backslash mode after 1 character.
				backslash--;
			
			switch (began) {
			case 0:
				if (Character.isWhitespace(c))
					// Trim whitespace at the start.
					continue;
				
				began = 1;
				break;
				
			case 1:
				began = 2;
			}
			
			if (c == '\r') {
				// Ignore carriage return.
				continue;
				
			} else if (c == '\'') {
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
					// Start literal mode with the same character
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
				
				if (c == '.') {
					// Special cases for dot.
					
					if (dot) {
						if (line.charAt(i - 1) == '.') {
							// Double dot.
							
							if (i - start > 1) {
								// There is a lexeme behind the double dot.
								// Return that first.
								return line.substring(start, i - 1);
							}
							
							// Return the double dot.
							return line.substring(start, i + 1);
						}
						
						// Dot acts as a separator.
						return line.substring(start, i - 1);
					}
					
					dot = true;
					
					if (i == start)
						// Just started. Wait for any leading characters.
						continue;

					String result = line.substring(start, i);
					
					if (!result.trim().matches("^-?[0-9]*\\.?[0-9]*$")) {
						// No longer a real number.
						// Return without the dot.
						return result;
					}
					
					// No problem so far. Continue.
					continue;
					
				} else {
					// Non-dot characters.
					
					if (Character.isWhitespace(c)) {
						// Don't care about the whitespace. Discard it.
						return line.substring(start, i + 1);
					
					} else if (Character.isDigit(c)) {
						// Get the lexeme before this character.
						String result = line.substring(start, i);
						
						if (result.matches("^-?[0-9]*\\.?[0-9]*$"))
							// A real number so far.
							continue;
						
						if (result.matches("[^a-zA-Z0-9_]"))
							// A special character was used
							// before identifier-safe characters.
							// Return everything before this character.
							return result;
						
						continue;
						
					} else if (Character.isLetter(c) || c == '_') {
						// Possible characters for identifiers.
						
						// Get the lexeme before this character.
						String result = line.substring(start, i);
						
						if (result.matches("[^a-zA-Z0-9_]"))
							// A special character was used
							// before identifier-safe characters.
							// Return everything before this character.
							return result;
						
						if (dot)
							// A dot was previously read. Return everything besides this character.
							return result;
						
						continue;
						
					} else if (Helper.contains(Resources.special_characters, c)) {
						// Special characters like semicolon, comma, parenthesis, and brackets.
						if (start == i)
							// Return the character only.
							return line.substring(start, i + 1);
						else
							// A lexeme is behind this character.
							return line.substring(start, i);
					} else {
						// Multi-line lexemes with special characters.
						
						String[][] specials = new String[][] {
							Resources.arithmetic_operators,
							Resources.relational_operators
						};
						
						Boolean flag = false;
						String cut = line.substring(start, i);
						
						for (String[] list : specials) {
							for (String lexeme : list)
								if (lexeme.startsWith(cut)) {
									flag = true;
									break;
								}
							
							if (flag)
								break;
						}
						
						if (flag)
							// A lexeme starts with these set of characters.
							// Continue.
							continue;
						
						String result = line.substring(start, i);
						
						if (result.matches(".*\\w.*"))
							// A word character is read.
							// Return everything before the special character.
							return result;
					}
				}
				
				// Nothing matched. Return up to the cursor's position.
				return line.substring(start, i + 1);
			}
		}
		
		if (comment != null)
			return comment += line.substring(start) + "\r\n";
		
		// Return the line if no terminating characters were found.
		return line.substring(start);
	}
	
	public String classify_lexeme(String lexeme) {
		if (Helper.contains(Resources.reserved, lexeme))
			return "reserved";
		
		if (Helper.contains(Resources.arithmetic_operators, lexeme))
			return "arithmetic operator";
		
		if (Helper.contains(Resources.relational_operators, lexeme))
			return "relational operator";
		
		if (Helper.contains(Resources.boolean_operators, lexeme))
			return "boolean operator";
		
		if (Helper.contains(Resources.data_types, lexeme))
			return "data type";
		
		if (lexeme.equals(";"))
			return "semicolon";
		
		if (lexeme.equals(":"))
			return "colon";
		
		if (lexeme.equals("."))
			return "dot";
		
		if (lexeme.equals(".."))
			return "double dot";
		
		if (lexeme.equals("("))
			return "open parenthesis";
		
		if (lexeme.equals(")"))
			return "close parenthesis";
		
		if (lexeme.equals(","))
			return "comma";
		
		if (lexeme.equals(":="))
			return "assignment";
		
		if (lexeme.equals("["))
			return "open bracket";
		
		if (lexeme.equals("]"))
			return "close bracket";
		
		if (lexeme.startsWith("{") && lexeme.endsWith("}"))
			return "comment";
		
		if (lexeme.startsWith("'"))
			if (lexeme.endsWith("'"))
				return "string";
			else
				return "broken string";
		
		if (lexeme.matches("^-?[0-9]*\\.\\.-?[0-9]*$"))
			return "array range";
		
		if (lexeme.matches("^-?[0-9]*$"))
			return "integer";

		if (lexeme.matches(Resources.REGEX_REAL))
			return "real";
		
		{
			Identifier identifier = Interpreter.identifiers().get(lexeme);
			
			if (identifier != null && identifier.isPredeclared())
				return "predeclared";
		}
		
		if (lexeme.matches("^[a-zA-Z_]+[a-zA-Z0-9_]*$"))
			return "identifier";
		
		return "unknown";
	}
	
	public int console_dump(String lexeme, String token_class) {
		MainWindow.appendConsoleText(lexeme + "			" + token_class);
		return 0;
	}
	
	public int file_dump(String lexeme, String token_class) {
		try (FileWriter fw = new FileWriter(Resources.OUTPUT_FILENAME, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw)) {
			out.println(lexeme + "	" + token_class);
		} catch(IOException e) {
			return -1;
		}
		
		return 0;
	}
	
	public void print_error(int code) {
		String message = "error code #" + code;
		
		if (code >= 0 && code < Resources.error_codes.length)
			message += "; " + Resources.error_codes[code];

		print_error(message);
	}
	
	public void print_error(String message) {
		MainWindow.appendConsoleText(
				"Error at line " + MainWindow.getLine() +
				"; " + message
		);
	}
	
	public void file_clear() {
		try {
			Files.deleteIfExists(Paths.get(Resources.OUTPUT_FILENAME));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void log(String text) {
		if (!MainWindow.verboseLog())
			return;
		
		MainWindow.appendConsoleText(text);
	}
}
