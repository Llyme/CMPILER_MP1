package main;
import java.util.ArrayList;
import java.util.Stack;

import identifier.IIdentifier;
import identifier.IdentifierBoolean;
import identifier.IdentifierFunction;
import identifier.IdentifierProcedure;
import identifier.IdentifierType;
import logic.BodyLogic;
import logic.FunctionLogic;
import logic.ProcedureLogic;
import logic.ProgramLogic;
import logic.VarLogic;

public class Scanner {
	private ArrayList<IIdentifier> identifiers = new ArrayList<IIdentifier>();
	/**
	 * If the scanner is in comment mode.
	 * This allows multiline comment across multiple feed.
	 */
	private String comment = null;
	private String programName = null;
	private String currentLine = null;
	private Stack<ScanMode> modes = new Stack<ScanMode>();
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
			null,
			"string"
		));
		identifiers.add(new IdentifierFunction(
			"write",
			true,
			null,
			"string",
			// Allows extending parameters,
			// where data type is equal to the last data type.
			(String)null
		));
		identifiers.add(new IdentifierType("char", true));
		identifiers.add(new IdentifierType("integer", true));
		identifiers.add(new IdentifierBoolean("false", true, false));
		identifiers.add(new IdentifierFunction("readln", true, null, "string"));
		identifiers.add(new IdentifierFunction("writeln", true, null, "string", (String)null));
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
	
	public void pushMode(ScanMode mode) {
		modes.push(mode);
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
		if (Helper.contains(Resources.reserved, lexeme))
			return "reserved";
		
		if (Helper.contains(Resources.arithmetic_operators, lexeme))
			return "arithmetic operator";
		
		if (Helper.contains(Resources.relational_operators, lexeme))
			return "relational operator";
		
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
		
		if (lexeme.startsWith("{") && lexeme.endsWith("}"))
			return "comment";
		
		if ((lexeme.startsWith("\"") && lexeme.endsWith("\"")) ||
			(lexeme.startsWith("'") && lexeme.endsWith("'")))
			return "literal";
		
		if (lexeme.matches("^-?[0-9]*$"))
			return "integer";

		{
			IIdentifier identifier = getIdentifier(lexeme);
			
			if (identifier != null && identifier.isPredeclared())
				return "predeclared";
		}
		
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
	
	public void print_error(int code) {
		MainWindow.appendConsoleText("Error at line " + MainWindow.getLine() + "; code " + code + ".");
		
		if (code >= 0 && code < Resources.error_codes.length)
			MainWindow.appendConsoleText(Resources.error_codes[code]);
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
		
		if ((result = ProgramLogic.parse(this, lexeme, token_class)) > 0)
			return result == 1;
		
		if ((result = VarLogic.parse(this, lexeme, token_class)) > 0)
			return result == 1;
		
		if ((result = ProcedureLogic.parse(this, lexeme, token_class)) > 0)
			return result == 1;
		
		if ((result = FunctionLogic.parse(this, lexeme, token_class)) > 0)
			return result == 1;
		
		if ((result = BodyLogic.parse(this, lexeme, token_class)) > 0)
			return result == 1;
		
		return false;
	}
}
