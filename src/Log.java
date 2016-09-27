import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Log {
	private static String logFile = null;
	private static ArrayList<String> log = new ArrayList<String>();
	
	public static void setLogFile(String path) {
		logFile = path;
	}
	
	public static synchronized void write(String s) {
		log.add(s);
	}
	
	public static synchronized void saveLog() {
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(logFile);
		} catch (FileNotFoundException e) {
			System.out.println("There was a problem with your file.\nUnfortunately you will not be able to save the log.");
		}
		PrintWriter writer = new PrintWriter(output, true);
		for(String s : log) {
			writer.println(s);
			System.out.println(s);
		}
		System.out.println("Log saved to " + logFile + ".");
		try {
			output.close();
		} catch (IOException e) {
		}
	}
}
