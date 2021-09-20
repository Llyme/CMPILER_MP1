package main;

public interface Condition {
	boolean invoke(String lexeme, String token_class);
}
