import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

//set starting preferences - # of customers, cashiers, and log files
public class StartingPreferences {
	private StartingPreferences() {
	}
	
	public static void main(String[] args) {
		start();
	}

	private static void start() {
		int nStations = chooseNumCashiers();
		String filePath = chooseLogFile();
		
		System.out.println("Creating the GUI...");
		
		Log.setLogFile(filePath);
		Feeder feeder = new Feeder(nStations);
		View.setModel(feeder);
		
		Customer[] c = new Customer[50];
		for (int i = 0; i < c.length; i++) {
			// customers have between 2 and 7 items
			c[i] = new Customer((int) (Math.random() * 6 + 2));
			feeder.newCustomer(c[i]);
		}

		feeder.start();

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				View.createAndShowGUI();
			}
		});
	}
	
	//choose a file to write logs to
	//returns the file path
	private static String chooseLogFile() {
		System.out.println("Please entire the full filepath of a text file to which a log will be saved.\nThe log will be saved whenever you press the appropriate button.");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		boolean goodFile = false;
		String response = null;
		
		while(!goodFile) {
			try {
				response = reader.readLine();
			} catch (IOException e) {
				System.out.println("Sorry, an error occurred. The program will now terminate.");
				System.exit(0);
			}
			
			goodFile = isValidFileName(response);
			if(!goodFile) {
				System.out.println("Sorry, that was an invalid filepath. Please try again.");
			}
		}
		
		return response;
	}
	
	private static boolean isValidFileName(String fileName) {
	    final File file = new File(fileName);
	    boolean isValid = true;
	    try {
	        if (file.createNewFile()) {
	            file.delete();
	        }
	    } catch (IOException e) {
	        isValid = false;
	    }
	    return isValid;
	}

	private static int chooseNumCashiers() {
		System.out.println("How many cashiers would you like? Recommended: between 4 and 9.");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		boolean isNumber = false;
		int nCashiers = 0;

		while (!isNumber) {
			String response = null;
			try {
				response = reader.readLine();
			} catch (IOException e) {
				System.out.println("Sorry, an error occurred. The program will now terminate.");
				System.exit(0);
			}
			try {
				nCashiers = Integer.valueOf(response); // see if this is an acceptable value - if not, exception is thrown
				isNumber = true;
			} catch (Exception e) { // if the user types a non-number, alert
									// them & remind them of their options
				System.out.println("Sorry, but \"" + response
						+ "\" is not a valid integer. Please try again.");
			}
		} //end of while loop
		
		return nCashiers;
	}

}
