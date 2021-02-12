/**
 * MilkWeight final project for A team 128
 * Project members: Joshua Faessler, Daniel Kouchekina, Ryan Sun, and Thiago Braga
 */


package application.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import application.Main;
import application.data.MilkData;
import application.gui.reports.AnnualReportPane;
import application.gui.reports.CustomDateRangeReportPane;
import application.gui.reports.FarmReportPane;
import application.gui.reports.MonthlyReportPane;
import application.gui.reports.ReportPane;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 
 * DashboardStage - The stage used to display the Milk Weight Dashboard
 * 
 * @author Daniel Kouchekinia (kouchekinia, 2020)
 *
 */
public class DashboardStage extends Stage {

	private static final String TITLE = "Milk Weight Dashboard";
	private static final String CSS_PATH = "/stylesheets/Dashboard.css";

	private TabPane tabPane;

	public DashboardStage(MilkData data, int initialDashboardTabIndex) {

		// Create the tab pane
		tabPane = new TabPane();
		// https://stackoverflow.com/questions/31531059/how-to-remove-close-button-from-tabs-in-javafx
		// Make it so that tabs can't be closed by the user
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		// Create the main scene of the stage
		Scene dashboardScene = new Scene(tabPane, 800, 500);

		// Add the stylesheet for Dashboard
		dashboardScene.getStylesheets().add(CSS_PATH);

		// Add each ReportPane to the tab pane
		tabPane.getTabs().addAll(new Tab("Farm Report", new FarmReportPane(data, this)),
				new Tab("Annual Report", new AnnualReportPane(data, this)),
				new Tab("Monthly Report", new MonthlyReportPane(data, this)),
				new Tab("Custom Date Range Report", new CustomDateRangeReportPane(data, this)));

		// Set the currently selected tab to the specified one
		tabPane.getSelectionModel().select(initialDashboardTabIndex);

		// Configure window

		// Add icons
		this.getIcons().addAll(Main.ICONS);

		this.setResizable(false);

		// Set the title of the window
		this.setTitle(TITLE);

		// Set the scene of the window to the main scene
		this.setScene(dashboardScene);

		// Request focus on the tab pane when the stage is shown
		Platform.runLater(() -> tabPane.requestFocus());
	}

	/**
	 * Reloads the data source, essentially re-opening the application but with the
	 * same files.
	 */
	public void refreshDataSource() {
		// Close window
		this.close();

		// Re-process data and show dash-board
		Main.loadDashboard(tabPane.getSelectionModel().getSelectedIndex());
	}

	/**
	 * Restarts the entire application.
	 */
	public void chooseNewDataSource() {
		// Close window
		this.close();

		// Restart application
		Main.startApplication(tabPane.getSelectionModel().getSelectedIndex());
	}

	/**
	 * Exports a given report to a text file chosen by the user
	 * 
	 * @param report The report to export
	 */
	public void exportReport(ReportPane report) {

		// Create the file chooser
		FileChooser chooser = new FileChooser();

		// Ensure the user can only save to text files
		FileChooser.ExtensionFilter CSVFilter = new FileChooser.ExtensionFilter("CSV file", "*.csv");
		chooser.getExtensionFilters().add(CSVFilter);
		chooser.setSelectedExtensionFilter(CSVFilter);

		// Set a default file name and default save directory
		chooser.setInitialFileName("Report.csv");
		chooser.setInitialDirectory(new File(System.getProperty("user.home")));

		// Prompt the user for a file save location
		File fileToSaveTo = chooser.showSaveDialog(this.getScene().getWindow());

		// If the user did not select a file or clicked cancel, do nothing
		if (fileToSaveTo == null)
			return;

		// Write to the file
		try {
			// Open a buffered writer
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileToSaveTo, false));

			// Write the text to the file, if it is not null
			String reportText = report.getTextReport();
			fileWriter.append(
					reportText != null ? reportText : "This report type has not been configured for text output.");

			// Close the writer
			fileWriter.close();

			// Alert the user the report has been successfully exported.
			Main.showAlert(AlertType.INFORMATION, "Report Successfully Exported",
					"Report has been successfully exported to '" + fileToSaveTo.getName() + "'", "");

		} catch (IOException e) {
			e.printStackTrace();

			// Show an alert to the user if there was an IOException
			Main.showAlert(AlertType.ERROR, "Could Not Export Report", "Could Not Export Report",
					"Unfortunately we could not export the selected report to the file specified.");
		}
	}

}
