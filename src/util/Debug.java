package util;

public class Debug {
	public static final boolean debug = true;

	public static void echo(String str) {
		if (debug)
			System.out.println(str);
	}

	public static void echo(String str, int d) {
		if (debug)
			System.out.println(str + d);
	}

	public static void echo(String str, double d) {
		if (debug)
			System.out.println(str + d);
	}

	public static void echo(String str, short d) {
		if (debug)
			System.out.println(str + d);
	}

	public static void echo(String str, String d) {
		if (debug)
			System.out.println(str + d);
	}
}
