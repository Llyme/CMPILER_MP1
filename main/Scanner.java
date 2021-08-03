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

public class Scanner {
	private ArrayList<IIdentifier> identifiers = new ArrayList<IIdentifier>();
	/**
	 * If the scanner is in comment mode.
	 * This allows multiline comment across multiple feed.
	 */
	private String comment = null;
	private String programName = null;
	private String currentLine = null;
	public Stack<ScanMode> modes = new Stack<ScanMode>();
	private DeclarationType declarationType = DeclarationType.Program;
	private BodyType bodyType = BodyType.Main;
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
	}
	
	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}
	
	public Boolean modeEmpty() {
		return modes.empty();
	}
	
	public ScanMode peekMode() {
		return modes.peek();
	}
	
	public ScanMode popMode() {
		return modes.pop();
	}
	
	/*public ScanMode popMode() {
		ScanMode result = modes.pop();
		String log = "Mode";
		
		for (ScanMode mode : modes)
			log += " -> " + mode;
		
		MainWindow.appendConsoleText(log);
		return result;
	}*/
	
	public void pushMode(ScanMode... modes) {
		for (ScanMode mode : modes)
			this.modes.push(mode);

		/*String log = "Mode";
		
		for (ScanMode mode : this.modes)
			log += " -> " + mode;
		
		MainWindow.appendConsoleText(log);*/
	}
	
	public DeclarationType getDeclarationType() {
		return declarationType;
	}
	
	public void setDeclarationType(DeclarationType type) {
		declarationType = type;
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
	
	public void setBodyType(BodyType type) {
		bodyType = type;
	}
	
	public BodyType getBodyType() {
		return bodyType;
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
			
			if (parse(lexeme, token_class)) {
				IdentifierProcedure target = getTargetProcedure();
				
				if (target != null)
					// Write into target procedure/function.
					if (targetProcedurePadding > 0)
						targetProcedurePadding--;
					else if (!token_class.equals("comment"))
						// Exclude comments because they do nothing.
						target.push(lexeme, token_class);
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
		Boolean began = false;
		/***
		 * 0 = Not Initialized;
		 * 1 = Not Special;
		 * 2 = Special;
		 */
		int special = 0;
		Character literal = null;
		int backslash = 0;
		
		for (int i = start; i < line.length(); i++) {
			char c = line.charAt(i);
			
			if (c == '.' && line.subSequence(start, i).equals("end"))
				// Special case for dot separator.
				return line.substring(start, i);
			
			if (backslash > 0)
				// End backslash mode after 1 character.
				backslash--;
			
			if (!began && Character.isWhitespace(c))
				// Trim whitespace at the start.
				continue;
			else
				began = true;
			
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
				if (special == 0)
					if (Character.isLetterOrDigit(c) || c == '_' || c == '.')
						special = 1;
					else
						special = 2;
				
				if (c == ';' ||
					c == ',' ||
					c == '(' ||
					c == ')')
					if (start == i)
						// Return the separator only.
						return line.substring(start, i + 1);
					else
						// A lexeme is behind this separator.
						return line.substring(start, i);
				
				if (Character.isWhitespace(c))
					// Don't care about the whitespace. Discard it.
					return line.substring(start, i + 1);
				
				switch (special) {
				case 1:
					if (!Character.isLetterOrDigit(c) && c != '_' && c != '.')
						return line.substring(start, i);
					break;
				case 2:
					if (Character.isLetterOrDigit(c) || c == '_' || c == '.')
						return line.substring(start, i);
					break;
				}
			}
		}
		
		if (comment != null)
			return comment += line.substring(start) + "\n";
		
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
				return "literal";
			else
				return "broken literal";
		
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
		String message =
			"Error at line " + MainWindow.getLine() +
			"; error code #" + code;
		
		if (code >= 0 && code < Resources.error_codes.length)
			message += "; " + Resources.error_codes[code];

		MainWindow.appendConsoleText(message);
		
		String log = "Mode";
		
		for (ScanMode mode : this.modes)
			log += " -> " + mode;

		MainWindow.appendConsoleText(log);
		MainWindow.appendConsoleText("");
	}
	
	public void file_clear() {
		try {
			Files.deleteIfExists(Paths.get(Resources.OUTPUT_FILENAME));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 0 = Skip;
	 * 1 = Accepted;
	 * 2 = Error;
	 */
	public Boolean parse(String lexeme, String token_class) {
		if (token_class.equals("comment"))
			// Comments are always ignored, regardless of where it was used.
			return true;
		
		int result;
		
		if ((result = GeneralLogic.parse(this, lexeme, token_class)) != 0)
			return result == 1;
		
		if ((result = ProgramLogic.parse(this, lexeme, token_class)) != 0)
			return result == 1;
		
		if ((result = VarLogic.parse(this, lexeme, token_class)) != 0)
			return result == 1;
		
		if ((result = ProcedureLogic.parse(this, lexeme, token_class)) != 0)
			return result == 1;
		
		if ((result = FunctionLogic.parse(this, lexeme, token_class)) != 0)
			return result == 1;
		
		if ((result = BodyLogic.parse(this, lexeme, token_class)) != 0)
			return result == 1;
		
		if ((result = ArithmeticLogic.parse(this, lexeme, token_class)) != 0)
			return result == 1;
		
		if ((result = ForLoopLogic.parse(this, lexeme, token_class)) != 0)
			return result == 1;
		
		if ((result = IfThenElseLogic.parse(this, lexeme, token_class)) != 0)
			return result == 1;
		
		if ((result = ExpressionLogic.parse(this, lexeme, token_class)) != 0)
			return result == 1;
		
		return false;
	}
}
