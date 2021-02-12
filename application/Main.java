/**
 * MilkWeight final project for A team 128
 * Project members: Joshua Faessler, Daniel Kouchekina, Ryan Sun, and Thiago Braga
 */

package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import application.data.MilkData;
import application.data.MilkEntry;
import application.gui.DashboardStage;
import application.gui.DataSelectStage;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

	// Statically keep track of the last data files so that the application
	// can be restarted with the previous data files.
	private static List<File> lastDataFiles;
	
	public static final List<Image> ICONS = Main.getIcons();

	/**
	 * Called by JavaFX when the program starts. Configures text rendering and calls
	 * startApplication()
	 * 
	 * @param arg0 Unused stage argument
	 */
	@Override
	public void start(Stage arg0) throws Exception {
		// Improved font rendering
		// https://stackoverflow.com/questions/18382969/can-the-rendering-of-the-javafx-2-8-font-be-improved
		System.setProperty("prism.lcdtext", "false");
		System.setProperty("prism.text", "t2k");

		// Start the application with the first report tab open initially
		startApplication(0);
	}

	/**
	 * Starts the application by opening the data select stage and subsequently the
	 * dash-board stage.
	 * 
	 * @param initialDashboardTabIndex The tab index the dashboard should be opened
	 *                                 to.
	 */
	public static void startApplication(int initialDashboardTabIndex) {
		// Create and show a data select stage
		DataSelectStage dataSelectStage = new DataSelectStage();
		dataSelectStage.show();

		// When files are selected...
		dataSelectStage.setOnFileSelect((List<File> files) -> {
			// Update mostRecentDataFiles
			lastDataFiles = files;

			// Close the data select stage
			dataSelectStage.close();

			// Load the dash-board
			loadDashboard(initialDashboardTabIndex);
		});
	}

	/**
	 * Processes the data found in the lastDataFiles static variable
	 * @return A MilkData object containing all of the data found
	 */
	private static MilkData processLastDataFiles() {
		MilkData data = new MilkData();

		int rejectedFiles = 0;
		int rejectedLines = 0;

		// Date format for reading dates in year-month-day format (Eg. 2019-1-2)
		DateFormat dateParser = new SimpleDateFormat("y-M-d");

		// Loop through all data files
		for (File dataFile : lastDataFiles) {
			try {
				Scanner scanner = new Scanner(dataFile);

				// Ensure the files passed meet the specification by checking the first row
				if (!scanner.hasNextLine() || !scanner.nextLine().strip().equals("date,farm_id,weight")) {
					rejectedFiles++;
					continue;
				}

				// Loop through each line of each file
				while (scanner.hasNextLine()) {
					String row[] = scanner.nextLine().strip().split(",");

					// Ensure there are three columns in each row
					if (row.length != 3) {
						rejectedLines++;
						continue;
					}

					try {
						// Parse the date from column 1
						Date date = dateParser.parse(row[0].strip());

						// Grab the farm ID from column 2
						String farmID = row[1].strip();

						// Parse the weight from column 3
						int weight = Integer.parseInt(row[2].strip());

						// Create a new MilkEntry and add it to MilkData
						data.addEntry(new MilkEntry(farmID, date, weight));

					} catch (ParseException | NumberFormatException e) {
						// If there was an error parsing the columns, note it in the total count
						rejectedLines++;
					}
				}

				// Close the scanner
				scanner.close();
			} catch (FileNotFoundException e) {
				// If the file could not be opened, note it in the total count
				rejectedFiles++;
			}
		}

		// Send the user an alert if files could not be read
		if (rejectedFiles > 0 || rejectedLines > 0) {
			String message = String.format("%d line%s and %d file%s could not be read.\n", rejectedLines,
					rejectedLines == 1 ? "" : "s", rejectedFiles, rejectedFiles == 1 ? "" : "s");
			Main.showAlert(AlertType.WARNING, "Some Information Could Not be Parsed",
					"Some Information Could Not Be Parsed", message);
		}

		// Sort all of the data after adding it to the data structure
		data.organize();

		return data;
	}

	/**
	 * Loads the dash-board view from the provided initial tab
	 * @param initialTab the initial tab of the dash-board
	 */
	public static void loadDashboard(int initialTab) {
		// Process data here from mostRecentDataFiles
		MilkData data = processLastDataFiles();

		// Check to ensure there is some valid data
		if (data.getValidYears().size() == 0) {
			Main.showAlert(AlertType.ERROR, "No valid data", "No valid data could be found.",
					"No valid data could be found in the files selected");
			Main.startApplication(initialTab);
		} else {
			// Create and show the dash-board
			DashboardStage dashboardStage = new DashboardStage(data, initialTab);
			dashboardStage.show();
		}	
	}

	/**
	 * Launches JavaFX
	 * 
	 * @param args Ignored command line arguments.
	 */
	public static void main(String[] args) {
		launch();
	}

	/**
	 * Presents an alert to the user.
	 * The alert will always stay on top of other windows.
	 * @param type The AlertType of the alert
	 * @param title The title of the alert
	 * @param headerText The header of the alert
	 * @param bodyText The body text of the alert
	 */
	public static void showAlert(AlertType type, String title, String headerText, String bodyText) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(bodyText);
		alert.show();

		// Keep alert on top
		// https://stackoverflow.com/questions/38799220/javafx-how-to-bring-dialog-alert-to-the-front-of-the-screen
		((Stage) alert.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
	}
	
	/**
	 * Final method for retrieving a list of all icon images
	 * @return a list of all icon images
	 */
	private static final List<Image> getIcons() {
		List<Image> icons = new ArrayList<Image>();
		icons.add(new Image(Main.class.getResourceAsStream("/assets/icons/icon-512.png")));
		icons.add(new Image(Main.class.getResourceAsStream("/assets/icons/icon-256.png")));
		icons.add(new Image(Main.class.getResourceAsStream("/assets/icons/icon-128.png")));
		icons.add(new Image(Main.class.getResourceAsStream("/assets/icons/icon-64.png")));
		return icons;
	}

}
