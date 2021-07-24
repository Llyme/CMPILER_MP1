package identifier;

import java.util.ArrayList;

import main.MainWindow;

public class IdentifierProcedure implements IIdentifier {
	private String name;
	private Boolean predeclared;
	private ArrayList<String> lexemes = new ArrayList<String>();
	private ArrayList<String> token_classes = new ArrayList<String>();
	private ArrayList<String> parameterTypes = new ArrayList<String>();
	
	public IdentifierProcedure
	(String name,
	Boolean predeclared,
	String... parameterTypes) {
		this.name = name;
		this.predeclared = predeclared;
		
		for (String text : parameterTypes)
			this.parameterTypes.add(text);
	}
	
	public String getName() {
		return name;
	}
	
	public String getDataType() {
		return "procedure";
	}
	
	public Boolean isPredeclared() {
		return predeclared;
	}

	/**
	 * Adds a lexeme-class pair.
	 */
	public void push(String lexeme, String token_class) {
		lexemes.add(lexeme);
		token_classes.add(token_class);
		MainWindow.appendConsoleText("WRITING: " + lexeme + ", " + token_class);
	}
	
	public String getLexeme(int i) {
		return lexemes.get(i);
	}

	public String getTokenClass(int i) {
		return token_classes.get(i);
	}
	
	public int parameterCount() {
		return parameterTypes.size();
	}
	
	public String getParameterType(int i) {
		return parameterTypes.get(i);
	}
	
	@Deprecated
	public Object getValue() {
		return null;
	}

	@Deprecated
	public void setValue(Object value) {
	}

	/**
	 * Functions are immutable.
	 */
	@Deprecated
	public Boolean isValid(String lexeme) {
		return false;
	}
}
