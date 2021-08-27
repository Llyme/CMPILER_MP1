package main;

public final class Helper {
	public static Boolean contains(String[] array, String value) {
		for (String item : array)
			if (item.equals(value))
				return true;
		
		return false;
	}
	
	public static Boolean contains(char[] array, char value) {
		for (char item : array)
			if (item == value)
				return true;
		
		return false;
	}
}
