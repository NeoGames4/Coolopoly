package misc;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Console {

	public static void log(String text) {
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		System.out.println(fmt.format(new Date()) + " > " + text);
	}
	
	public static void err(String text) {
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		System.err.println(fmt.format(new Date()) + " > " + text);
	}

}
