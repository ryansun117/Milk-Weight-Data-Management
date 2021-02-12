/**
 * MilkWeight final project for A team 128
 * Project members: Joshua Faessler, Daniel Kouchekina, Ryan Sun, and Thiago Braga
 */


package application.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 
 * DataSelectStage - Represents a Data Selection Stage Note that this stage will not be shown unless
 * show() is explicitly called
 * 
 * @author Daniel Kouchekinia (kouchekinia, 2020)
 *
 */
public class DataSelectStage extends Stage {

	private Scene scane;
	private File recentFile;
	private FileSelectEventHandler fileSelectEventHandler;

	private static final String TITLE = "Select a Data Source";
	private static final String NO_RECENT_FILES_PLACEHOLDER = "No recently opened files could be found.";
	private static final String RECENTLY_USED_FILE_PATH = "./recently-used.txt";
	private static final String CSS_PATH = "/stylesheets/DataSelect.css";

	/**
	 * Creates a data selection stage.
	 * 
	 * @param fileSelectEventHandler The event to be called when the user selects file(s) top open.
	 */
	public DataSelectStage() {

		// Create the recentFile if it does not already exist
		recentFile = new File(RECENTLY_USED_FILE_PATH);
		try {
			recentFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set up Border Pane
		BorderPane layout = new BorderPane();

		// Set up Scene
		scane = new Scene(layout, 400, 500);
		scane.getStylesheets().add(CSS_PATH);

		// Add label to the top of the BorderPanem through a GridPane so the
		// label can be centered
		GridPane selectLabelGrid = new GridPane();
		layout.setTop(selectLabelGrid);
		Label selectLabel = new Label(TITLE);
		selectLabelGrid.add(selectLabel, 0, 0);
		selectLabel.getStyleClass().add("title");


		// In the middle, add a VBox for everything related to recently used files
		VBox recentlyUsedLayout = new VBox(10);
		layout.setCenter(recentlyUsedLayout);

		// Add recently used label
		Label recentlyUsedLabel = new Label("Recently Used:");
		recentlyUsedLayout.getChildren().add(recentlyUsedLabel);

		// Add recently used list view
		ListView<File> recentlyUsedListView =
				new ListView<File>(getRecentlyUsedFiles());
		// Select the first item by default
		recentlyUsedListView.getSelectionModel().select(0);
		// Allow the user to select multiple files
		recentlyUsedListView.getSelectionModel()
				.setSelectionMode(SelectionMode.MULTIPLE);
		// Add placeholder if the list is empty (otherwise the view becomes all white)
		if (recentlyUsedListView.getItems().isEmpty()) {
			recentlyUsedListView.getItems().add(new File(NO_RECENT_FILES_PLACEHOLDER));
			recentlyUsedListView.setDisable(true);
		}
		recentlyUsedLayout.getChildren().add(recentlyUsedListView);

		// Add recently used button
		Button selectRecentlyUsedButton = new Button("Open Selected");
		// Add an action when the user presses the select recently used button
		selectRecentlyUsedButton.setOnAction((ActionEvent) -> {
			// Call the files selected event
			if(fileSelectEventHandler != null)
				fileSelectEventHandler.filesSelected(recentlyUsedListView
						.getSelectionModel().getSelectedItems());
		});
		// Disable or enable the button based on whether or not the list of selected items isempty
		// https://stackoverflow.com/questions/42168434/how-to-listen-for-a-selection-change-in-a-javafx-listview
		recentlyUsedListView.getSelectionModel().getSelectedItems()
				.addListener((Change<? extends File> e) -> {
					// Disable the selection button if nothing was selected
					selectRecentlyUsedButton.setDisable(e.getList().isEmpty());
				});
		// Select button should be disabled by device
		selectRecentlyUsedButton
				.setDisable(recentlyUsedListView.getSelectionModel().isEmpty());
		recentlyUsedLayout.getChildren().add(selectRecentlyUsedButton);


		// At the bottom, add a VBox to contain everything related to opening new files
		VBox newLocationLayout = new VBox();
		layout.setBottom(newLocationLayout);
		newLocationLayout.setFillWidth(true);

		// Add a separator
		newLocationLayout.getChildren().add(new Separator());

		// Add a button to select new files
		Button fileSelectButton = new Button("Select a New Data Source");
		fileSelectButton.getStyleClass().add("fileSelect");
		newLocationLayout.getChildren().add(fileSelectButton);
		// Call openNewLocation() when the button is pressed
		fileSelectButton.setOnAction((ActionEvent h) -> openNewLocation());


		// Configure window
		this.getIcons().addAll(Main.ICONS);
		
		this.setTitle(TITLE);
		this.setScene(scane);
		this.setResizable(false);
	}
	
	/**
	 * Sets the on file select event handler
	 * @param eventHandler A FileSelectEventHandler
	 */
	public void setOnFileSelect(FileSelectEventHandler eventHandler) {
		this.fileSelectEventHandler = eventHandler;
	}

	/**
	 * Reads a list of files in from the recent file
	 * 
	 * @return A list of files read in from the recent file
	 */
	private ObservableList<File> getRecentlyUsedFiles() {
		Scanner fileScanner;
		ObservableList<File> fileList = FXCollections.observableArrayList();

		try {
			fileScanner = new Scanner(recentFile);

			// Each file is its own line
			while (fileScanner.hasNextLine()) {
				// Create a file from each line
				File recentlyUsed = new File(fileScanner.nextLine());
				// Add the file, if it exists, the beginning of the list
				if (recentlyUsed.exists())
					fileList.add(0, recentlyUsed);
			}

			fileScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return fileList;
	}

	/**
	 * Private method which gets called when the "Select a New Data Source" button is pressed.
	 */
	private void openNewLocation() {
		// Create and configure a file chooser dialogue
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select a New Data Source");
		// Make it so that the user can only select .csv files
		FileChooser.ExtensionFilter CSVFilter =
				new FileChooser.ExtensionFilter("CSV files", "*.csv");
		chooser.getExtensionFilters().add(CSVFilter);
		chooser.setSelectedExtensionFilter(CSVFilter);

		// Open the dialogue and read the files into a list
		List<File> fileList = chooser.showOpenMultipleDialog(this);

		// Do nothing if the user selected no files or clicked cancel
		if (fileList == null || fileList.isEmpty())
			return;

		// Write each file selected into the recent file
		try {
			// Open the recent file
			BufferedWriter writer =
					new BufferedWriter(new FileWriter(recentFile, true));

			// Append each file's path into the recent file
			for (File f : fileList)
				writer.append(f.getAbsolutePath() + "\n");

			// Close the recent file
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Call the files selected event
		if(fileSelectEventHandler != null)
			fileSelectEventHandler.filesSelected(fileList);
	}

}
