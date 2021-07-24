package main;

public final class Helper {
	public static Boolean contains(String[] array, String value) {
		for (String string : array)
			if (string.equals(value))
				return true;
		
		return false;
	}
}
