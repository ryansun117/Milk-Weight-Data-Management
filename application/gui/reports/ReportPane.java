/**
 * MilkWeight final project for A team 128
 * Project members: Joshua Faessler, Daniel Kouchekina, Ryan Sun, and Thiago Braga
 */


package application.gui.reports;

import application.data.MilkData;
import application.gui.DashboardStage;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * 
 * ReportPane - Abstract class used to display individual reports
 * 
 * @author Daniel Kouchekinia (kouchekinia, 2020)
 *
 */
public abstract class ReportPane extends Pane {

	protected VBox optionsPane;
	protected GridPane reportPane;

	/**
	 * Initialize a new ReportPane with the specified data and stage
	 * 
	 * @param data  The {@link MilkData} data object
	 * @param stage The {@link DashboardStage} the report is in
	 */
	public ReportPane(MilkData data, DashboardStage stage) {

		// Add a grid to the pane
		GridPane grid = new GridPane();
		this.getChildren().add(grid);

		// Add a border pane for the left-hand-side options pane
		BorderPane optionsBorderPane = new BorderPane();
		optionsBorderPane.getStyleClass().add("optionsBorderPane");
		grid.add(optionsBorderPane, 0, 0);

		// Add the individualized options pane to the center of the options border pane
		this.optionsPane = new VBox(5);
		optionsPane.getStyleClass().add("optionsPane");
		optionsBorderPane.setCenter(optionsPane);

		// The options pane should be set at 200 width
		optionsPane.setMinWidth(200);
		optionsPane.setMaxWidth(200);

		// https://stackoverflow.com/questions/33414194/fill-width-in-a-pane?rq=1
		// Make the pane fill the height of the page
		optionsBorderPane.prefHeightProperty().bind(this.heightProperty());

		// Add a buttons VBox to the bottom of the options border pane
		VBox bottomButtonsVBox = new VBox(5);
		bottomButtonsVBox.getStyleClass().add("bottomButtonsVBox");
		optionsBorderPane.setBottom(bottomButtonsVBox);

		// Add export report button
		Button exportButton = new Button("Export Report");
		bottomButtonsVBox.getChildren().add(exportButton);
		exportButton.getStyleClass().add("bottomButton");
		exportButton.setOnAction((ActionEvent) -> stage.exportReport(this));

		// Add data source menu button button
		MenuButton dataSourceMenuButton = new MenuButton("Data Source");
		bottomButtonsVBox.getChildren().add(dataSourceMenuButton);
		dataSourceMenuButton.getStyleClass().add("bottomButton");

		// Add refresh data item in the data source menu button
		MenuItem refreshItem = new MenuItem("Refresh Data");
		refreshItem.setOnAction((ActionEvent) -> stage.refreshDataSource());
		dataSourceMenuButton.getItems().add(refreshItem);

		// Add change data source item in the data source menu button
		MenuItem changeSrcItem = new MenuItem("Change Data Source");
		changeSrcItem.setOnAction((ActionEvent) -> stage.chooseNewDataSource());
		dataSourceMenuButton.getItems().add(changeSrcItem);

		// Add the individualized report grid pane to the right hand side of the screen
		this.reportPane = new GridPane();
		reportPane.getStyleClass().add("reportPane");
		grid.add(reportPane, 1, 0);
	}

	/**
	 * Returns a text-based version of the report
	 * 
	 * @return A text-based version of the report
	 */
	public abstract String getTextReport();

}
