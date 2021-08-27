package main;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;

import identifier.*;
import logic.*;
import node.*;

public class Scanner {
	private ArrayList<IIdentifier> identifiers = new ArrayList<IIdentifier>();
	/**
	 * If the scanner is in comment mode.
	 * This allows multiline comment across multiple feed.
	 */
	private String comment = null;
	private String programName = null;
	private String currentLine = null;
	private Boolean waitingUserInput = false;
	/**
	 * The latest user input when `waitingUserInput` was enabled.
	 */
	private String userInput = null;
	/**
	 * Currently targeted identifier for assignment.
	 */
	private IIdentifier targetIdentifier = null;
	/**
	 * Currently targeted procedure or function on construction.
	 */
	private IdentifierProcedure targetProcedure = null;
	/**
	 * Prevents the first few lexeme-class pair from being written.
	 */
	private int targetProcedurePadding = 2;
	/**
	 * A stack of procedures/functions being prioritized
	 * before going back to the main program.
	 */
	private Stack<IdentifierProcedure> procedureStack = new Stack<IdentifierProcedure>();
	/**
	 * An array of identifiers queued for variable declaration.
	 */
	private ArrayList<String> identifierQueue = new ArrayList<String>();
	
	public Scanner() {
		// Initialize predefined identifiers.
		identifiers.add(new IdentifierType("boolean", true));
		identifiers.add(new IdentifierType("real", true));
		identifiers.add(new IdentifierBoolean("true", true, true));
		identifiers.add(new IdentifierFunction(
			"read",
			true,
			false,
			"" // Passing an empty string ignores data type.
		));
		identifiers.add(new IdentifierProcedure(
			"write",
			true,
			true,
			""
		));
		identifiers.add(new IdentifierType("char", true));
		identifiers.add(new IdentifierType("integer", true));
		identifiers.add(new IdentifierBoolean("false", true, false));
		identifiers.add(new IdentifierFunction("readln", true, false, null, ""));
		identifiers.add(new IdentifierProcedure("writeln", true, true, ""));
		identifiers.add(new IdentifierType("string", true));
		Parser.node = RootLogic.declare;
	}
	
	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}
	
	public IIdentifier getIdentifier(String name) {
		for (IIdentifier identifier : identifiers)
			if (identifier.getName().equals(name))
				return identifier;
		
		return null;
	}
	
	public void addIdentifierQueue(String name) {
		identifierQueue.add(name);
	}
	
	public void clearIdentifierQueue() {
		identifierQueue.clear();
	}
	
	public ArrayList<String> getIdentifierQueue() {
		return identifierQueue;
	}
	
	public void addIdentifier(IIdentifier identifier) {
		identifiers.add(identifier);
	}
	
	public Boolean getWaitingUserInput() {
		return waitingUserInput;
	}
	
	public void waitForUserInput() {
		waitingUserInput = true;
	}
	
	public String getUserInput() {
		return userInput;
	}
	
	public IIdentifier getTargetIdentifier() {
		return targetIdentifier;
	}
	
	public void setTargetIdentifier(IIdentifier identifier) {
		targetIdentifier = identifier;
	}
	
	public IdentifierProcedure getTargetProcedure() {
		return targetProcedure;
	}
	
	public void setTargetProcedure(IdentifierProcedure value) {
		targetProcedurePadding = 2;
		targetProcedure = value;
	}
	
	public String getCurrentLine() {
		return currentLine;
	}
	
	public Boolean procedureStackEmpty() {
		return procedureStack.isEmpty();
	}
	
	public IdentifierProcedure procedureStackPeek() {
		return procedureStack.peek();
	}
	
	public IdentifierProcedure popProcedureStack() {
		return procedureStack.pop();
	}
	
	public void pushProcedureStack(IdentifierProcedure value) {
		procedureStack.push(value);
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
			console_dump(lexeme, token_class);
			file_dump(lexeme, token_class);
			boolean flag = Parser.parse(lexeme, token_class);
			log("[Result] " + flag);
			log("");
			
			if (flag) {
				IdentifierProcedure target = getTargetProcedure();
				
				if (target != null)
					// Write into target procedure/function.
					if (targetProcedurePadding > 0)
						targetProcedurePadding--;
					else if (!token_class.equals("comment"))
						// Exclude comments because they do nothing.
						target.push(lexeme, token_class);
			} else {
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
				} else if (i - start >= 1) {
					// Started a literal when there are non-whitespace before it.
					String result = line.substring(start, i).trim();
					
					if (result.length() > 0)
						return result;
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
								// There is a lexeme behind the double dot. Return that first.
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

					if (line.substring(start, i).trim().matches(".*[^0-9].*"))
						// There are non-digits in the capture. Return without the dot.
						return line.substring(start, i);
					
				} else {
					// Non-dot characters.
					
					if (Character.isWhitespace(c)) {
						// Don't care about the whitespace. Discard it.
						return line.substring(start, i + 1);
					
					} else if (Character.isDigit(c)) {
						// No problems involved with digits.
						continue;
						
					} else if (Character.isLetterOrDigit(c) || c == '_') {
						// Possible characters for identifiers.
						
						if (dot)
							// A dot was previously read. Return everything besides this character.
							return line.substring(start, i);
						
					} else if (Helper.contains(Resources.special_characters, c)) {
						// Special characters like semicolon, comma, parenthesis, and brackets.
						if (start == i)
							// Return the character only.
							return line.substring(start, i + 1);
						else
							// A lexeme is behind this character.
							return line.substring(start, i);
					}
				}
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

		if (lexeme.matches("^-?[0-9]*\\.?[0-9]*$"))
			return "real";
		
		{
			IIdentifier identifier = getIdentifier(lexeme);
			
			if (identifier != null && identifier.isPredeclared())
				return "predeclared";
		}
		
		if (lexeme.matches("^[a-zA-Z0-9_]*$"))
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
