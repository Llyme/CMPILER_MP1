package main;

public class LexemeTokenPair {
	public static final LexemeTokenPair EMPTY = new LexemeTokenPair("", "");
	
	private String lexeme;
	private String token;
	
	public LexemeTokenPair(String lexeme, String token) {
		this.lexeme = lexeme;
		this.token = token;
	}
	
	public String lexeme() {
		return lexeme;
	}
	
	public String token() {
		return token;
	}
}
