/**
 * MilkWeight final project for A team 128
 * Project members: Joshua Faessler, Daniel Kouchekina, Ryan Sun, and Thiago Braga
 */


package application.gui.reports;

import java.text.DecimalFormat;
import java.util.List;

import application.data.MilkData;
import application.data.MilkEntry;
import application.gui.DashboardStage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Filename: FarmReportPane.java Project: cs400-MilkWeight Course: cs400
 * Authors: Ryan Swiersz
 * 
 * The purpose of this class is to generate a GUI display once the user selects
 * valid inputs from the drop-down menus and clicks the "Apply" button. After
 * this, the user has the ability to generate a file named "Report.txt" that
 * will store relevant information to the selected inputs.
 * 
 * @extends ReportPane
 */
public class FarmReportPane extends ReportPane {

	// Field variables
	private final String MONTHS[] = { "January", "February", "March", "April", "May", "June", "July", "August",
			"September", "October", "November", "December" };
	private static DecimalFormat df = new DecimalFormat("0.00");
	private ComboBox<String> farmID;
	private ComboBox<String> year;
	private Button apply;
	private String currentYear;
	private String currentFarmID;
	private MilkData data;
	@SuppressWarnings("rawtypes")
	private XYChart.Series dataSeries;
	private int[] monthValues;

	private TableView<TableData> table;
	private TableColumn<TableData, String> column1;
	private TableColumn<TableData, Double> column2;
	private TableColumn<TableData, Double> column3;
	@SuppressWarnings("rawtypes")
	private BarChart barChart;
	private int totalWeight;
	private Label totalLabel;

	/**
	 * The purpose of this inner-class is to help organize the data that will be
	 * used inside of the GUI Table
	 * 
	 * @author swiersz
	 *
	 */
	public class TableData {

		// Field variables
		private String month;
		private double data;
		private Double percent;

		/**
		 * Constructor that takes three relevant inputs
		 * 
		 * @param month   - month name
		 * @param data    - weight related to the month
		 * @param percent - percent of the annual total for the month
		 */
		public TableData(String month, double data, double percent) {
			this.month = month;
			this.data = data;
			this.percent = percent;
		}

		/**
		 * Simple getter for month
		 * 
		 * @return the month field
		 */
		public String getMonth() {
			return month;
		}

		/**
		 * Simple getter for data
		 * 
		 * @return the data field
		 */
		public double getData() {
			return data;
		}

		/**
		 * Simple getter for percent
		 * 
		 * @return the percent field
		 */
		public double getPercent() {
			return percent;
		}

	} // End TableData
	
	/**
	 * The constructor for FarmReportPane that takes two arguments
	 * 
	 * @param data  - The relevant data being passed in
	 * @param stage - The stage in which the GUI will be displayed
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public FarmReportPane(MilkData data, DashboardStage stage) {
		// super call
		super(data, stage);
		this.data = data;

		// Initialize month values
		monthValues = new int[12];

		// Initialize field for farmIDs
		farmID = new ComboBox<String>(); // ComboBox for Farm ID selection
		List<String> farmIDs = data.getFarmIDs();
		ObservableList farmList = FXCollections.observableList(farmIDs);
		farmID.setItems(farmList);
		farmID.getSelectionModel().selectFirst();
		farmID.setOnAction((e) -> inputsUpdated());
		farmID.setMaxWidth(Double.MAX_VALUE); // Formatting for the ComboBox

		// Initialize field for year selection
		year = new ComboBox<String>(); // ComboBox for Farm ID selection
		List<Integer> years = data.getValidYears();
		ObservableList yearsList = FXCollections.observableList(years);
		year.setItems(yearsList);
		year.getSelectionModel().selectFirst();
		year.setOnAction((e) -> inputsUpdated());
		year.setMaxWidth(Double.MAX_VALUE); // Formatting for the ComboBox

		// Initialize apply button
		apply = new Button("Apply");
		apply.setMaxWidth(Double.MAX_VALUE);
		apply.setOnAction((e) -> handle()); // On button press

		// Setup Axis for bar graph
		NumberAxis dataAxis = new NumberAxis();
		dataAxis.setLabel("Weight");
		CategoryAxis monthAxis = new CategoryAxis();
		monthAxis.setLabel("Month");

		// Initialize barChart
		barChart = new BarChart(dataAxis, monthAxis);
		dataSeries = new XYChart.Series();
		// Insert data for barChart
		dataSeries.getData().add(new XYChart.Data(monthValues[0], MONTHS[0]));
		dataSeries.getData().add(new XYChart.Data(monthValues[1], MONTHS[1]));
		dataSeries.getData().add(new XYChart.Data(monthValues[2], MONTHS[2]));
		dataSeries.getData().add(new XYChart.Data(monthValues[3], MONTHS[3]));
		dataSeries.getData().add(new XYChart.Data(monthValues[4], MONTHS[4]));
		dataSeries.getData().add(new XYChart.Data(monthValues[5], MONTHS[5]));
		dataSeries.getData().add(new XYChart.Data(monthValues[6], MONTHS[6]));
		dataSeries.getData().add(new XYChart.Data(monthValues[7], MONTHS[7]));
		dataSeries.getData().add(new XYChart.Data(monthValues[8], MONTHS[8]));
		dataSeries.getData().add(new XYChart.Data(monthValues[9], MONTHS[9]));
		dataSeries.getData().add(new XYChart.Data(monthValues[10], MONTHS[10]));
		dataSeries.getData().add(new XYChart.Data(monthValues[11], MONTHS[11]));

		// Add data
		barChart.getData().add(dataSeries);
		barChart.setMaxWidth(300);
		barChart.setMaxHeight(400);

		// Initialize TableView
		table = new TableView();

		// Month column for table
		column1 = new TableColumn<>("Month");
		column1.setCellValueFactory(new PropertyValueFactory("month"));

		// Data column for table
		column2 = new TableColumn<>("Total Wt.");
		column2.setCellValueFactory(new PropertyValueFactory("data"));

		// Percent column for table
		column3 = new TableColumn<>("% Total");
		column3.setCellValueFactory(new PropertyValueFactory("percent"));

		// Add columns to the table
		table.getColumns().addAll(column1, column2, column3);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Remove excess columns

		// Add all the relevant nodes to the optionsPane
		this.optionsPane.getChildren().addAll(new Label("Farm ID:"), farmID);
		this.optionsPane.getChildren().addAll(new Label("Year:"), year);
		this.optionsPane.getChildren().addAll(new Label(""), apply);

		// Label initialization
		totalLabel = new Label("Overall Total Weight: " + totalWeight);

		// Add all the relevant nodes to the reportPane
		this.reportPane.add(barChart, 1, 0);
		this.reportPane.add(table, 0, 0);
		this.reportPane.add(totalLabel, 0, 1);
		
		// Initialize everything with default inputs
		inputsUpdated();
		
		if(!apply.isDisabled())
			// Show the data when the view loads
			Platform.runLater(() -> handle());
		
	} // End Constructor

	/**
	   * Updates applyButton disabled status based on whether or not valid input has been entered
	   */
	private void inputsUpdated() {
		apply.setDisable(farmID.getValue() == null || year.getValue() == null);
	}

	// Helper method to store all TableData objects into an ObservableList
	private ObservableList<TableData> getTableData() {
		ObservableList<TableData> list = FXCollections.observableArrayList();
		list.add(new TableData(MONTHS[0], monthValues[0],
				Double.parseDouble(df.format((double) monthValues[0] / totalWeight * 100))));
		list.add(new TableData(MONTHS[1], monthValues[1],
				Double.parseDouble(df.format((double) monthValues[1] / totalWeight * 100))));
		list.add(new TableData(MONTHS[2], monthValues[2],
				Double.parseDouble(df.format((double) monthValues[2] / totalWeight * 100))));
		list.add(new TableData(MONTHS[3], monthValues[3],
				Double.parseDouble(df.format((double) monthValues[3] / totalWeight * 100))));
		list.add(new TableData(MONTHS[4], monthValues[4],
				Double.parseDouble(df.format((double) monthValues[4] / totalWeight * 100))));
		list.add(new TableData(MONTHS[5], monthValues[5],
				Double.parseDouble(df.format((double) monthValues[5] / totalWeight * 100))));
		list.add(new TableData(MONTHS[6], monthValues[6],
				Double.parseDouble(df.format((double) monthValues[6] / totalWeight * 100))));
		list.add(new TableData(MONTHS[7], monthValues[7],
				Double.parseDouble(df.format((double) monthValues[7] / totalWeight * 100))));
		list.add(new TableData(MONTHS[8], monthValues[8],
				Double.parseDouble(df.format((double) monthValues[8] / totalWeight * 100))));
		list.add(new TableData(MONTHS[9], monthValues[9],
				Double.parseDouble(df.format((double) monthValues[9] / totalWeight * 100))));
		list.add(new TableData(MONTHS[10], monthValues[10],
				Double.parseDouble(df.format((double) monthValues[10] / totalWeight * 100))));
		list.add(new TableData(MONTHS[11], monthValues[11],
				Double.parseDouble(df.format((double) monthValues[11] / totalWeight * 100))));

		return list; // Return the list
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void handle() {
		table.refresh(); // reset the table each apply
		
		// initialize relevant variables
		this.currentFarmID = farmID.getValue();
		this.currentYear = String.valueOf(year.getValue());
		
		dataSeries.setName(currentYear);
		totalWeight = 0;

		for (int j = 0; j < 12; j++) {
			List<MilkEntry> monthsTotal = data.getEntriesForMonth(Integer.parseInt(currentYear), j, currentFarmID);
			int counter = 0;
			for (int i = 0; i < monthsTotal.size(); i++) {
				String result = monthsTotal.get(i).toString();
				String weight[] = result.split(","); // split at comma
				String total[] = weight[1].split(":"); // split at colon
				counter = counter + Integer.parseInt(total[1].trim());
			}
			monthValues[j] = counter;
		}
		for (int i = 0; i < monthValues.length; i++) {
			totalWeight = totalWeight + monthValues[i];
		}

		// Insert data for barChart
		dataSeries.getData().add(new XYChart.Data(monthValues[0], MONTHS[0]));
		dataSeries.getData().add(new XYChart.Data(monthValues[1], MONTHS[1]));
		dataSeries.getData().add(new XYChart.Data(monthValues[2], MONTHS[2]));
		dataSeries.getData().add(new XYChart.Data(monthValues[3], MONTHS[3]));
		dataSeries.getData().add(new XYChart.Data(monthValues[4], MONTHS[4]));
		dataSeries.getData().add(new XYChart.Data(monthValues[5], MONTHS[5]));
		dataSeries.getData().add(new XYChart.Data(monthValues[6], MONTHS[6]));
		dataSeries.getData().add(new XYChart.Data(monthValues[7], MONTHS[7]));
		dataSeries.getData().add(new XYChart.Data(monthValues[8], MONTHS[8]));
		dataSeries.getData().add(new XYChart.Data(monthValues[9], MONTHS[9]));
		dataSeries.getData().add(new XYChart.Data(monthValues[10], MONTHS[10]));
		dataSeries.getData().add(new XYChart.Data(monthValues[11], MONTHS[11]));

		// Add items to the table using helper method
		table.setItems(getTableData());

		// reset label and add new value to it
		this.reportPane.getChildren().remove(totalLabel);
		totalLabel = new Label("Overall Total Weight: " + totalWeight);
		this.reportPane.add(totalLabel, 0, 1);
	}

	@Override
	public String getTextReport() {
		String output = "Month,Month Weight,Percentage of Total\n";
		for (int i = 0; i < MONTHS.length; i++) {
			output = output + MONTHS[i] + "," + monthValues[i] + ","
					+ df.format((double) monthValues[i] / totalWeight * 100) + "\n";
		}
		output = output + currentFarmID + "," + currentYear + "," + totalWeight;
		return output;
	} // End getTextReport

} // End FarmReportPane