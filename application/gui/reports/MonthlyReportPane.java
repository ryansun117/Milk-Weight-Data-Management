/**
 * MilkWeight final project for A team 128
 * Project members: Joshua Faessler, Daniel Kouchekina, Ryan Sun, and Thiago Braga
 */

package application.gui.reports;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import application.data.MilkData;
import application.data.MilkEntry;
import application.gui.DashboardStage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

/**
 * 
 * MonthlyReportPane - displays a MonthlyReport pane, shows data for specified
 * month and year
 * 
 * @author Xuxiang Sun (2020)
 *
 */
public class MonthlyReportPane extends ReportPane {
	private final ComboBox<String> yearSelect; // ComboBox that stores user selected year
	private final ComboBox<String> monthSelect; // ComboBox that stores user selected month
	private final Button applyButton; // apply changes button
	private final TableView<Entry> table; // table used to display data
	private final PieChart piechart; // pie chart used to display data
	MilkData data; // stores data passed in

	/**
	 * 
	 * Entry - private inner class, stores MilkEntry to be used in display
	 * 
	 * @author Xuxiang Sun (2020)
	 *
	 */
	public class Entry {
		private SimpleStringProperty id; // Farm ID
		private SimpleIntegerProperty weight; // milk weight
		private SimpleDoubleProperty percent; // percentage

		/**
		 * Constructor for Entry class
		 * 
		 * @param id      - Farm ID
		 * @param weight  - milk weight
		 * @param percent - percentage
		 */
		public Entry(String id, int weight, double percent) {
			this.id = new SimpleStringProperty(id);
			this.weight = new SimpleIntegerProperty(weight);
			this.percent = new SimpleDoubleProperty(percent);
		}

		/**
		 * getter for id
		 * 
		 * @return id
		 */
		public String getId() {
			return id.get();
		}

		/**
		 * setter for id
		 * 
		 * @param s - String to be set
		 */
		public void setId(String s) {
			id.set(s);
		}

		/**
		 * getter for weight
		 * 
		 * @return weight
		 */
		public int getWeight() {
			return weight.get();
		}

		/**
		 * setter for weight
		 * 
		 * @param s - String to be set
		 */
		public void setWeight(int s) {
			weight.set(s);
		}

		/**
		 * getter for percent
		 * 
		 * @return percent
		 */
		public double getPercent() {
			// Return rounded
			return Math.round(percent.get() * 1000) / 1000.0d;
		}

		/**
		 * setter for percent
		 * 
		 * @param s - String to be set
		 */
		public void setPercent(double s) {
			percent.set(s);
		}
	}

	/**
	 * Constructor that displays the GUI
	 * 
	 * @param data  - MilkData that is passed in, contains all data
	 * @param stage - used to display the GUI
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MonthlyReportPane(MilkData data, DashboardStage stage) {
		super(data, stage); // super call
		this.data = data; // saves MilkData passed in

		this.yearSelect = new ComboBox<String>(); // ComboBox for Year selection
		// adds years in ComboBox
		for (Integer year : data.getValidYears()) {
			this.yearSelect.getItems().addAll(year.toString());
		}
		yearSelect.getItems().add("2000");
		this.yearSelect.setMaxWidth(Double.MAX_VALUE);
		this.yearSelect.getSelectionModel().selectFirst();
		this.yearSelect.setOnAction((e) -> updateValidMonths());
		this.optionsPane.getChildren().addAll(new Label("Year:"), yearSelect);

		this.monthSelect = new ComboBox<String>(); // ComboBox for Month selection

		this.monthSelect.setMaxWidth(Double.MAX_VALUE);
		this.monthSelect.setOnAction((e) -> monthSelectionChanged());
		this.optionsPane.getChildren().addAll(new Label("Month:"), monthSelect);

		// ComboBox<String> idSelect = new ComboBox<String>(); // ComboBox for Farm ID
		// selection
		// idSelect.setPromptText("Select Farm ID");
		// idSelect.setMaxWidth(Double.MAX_VALUE);
		// this.optionsPane.getChildren().addAll(new Label("Farm ID"), idSelect);

		this.applyButton = new Button("Apply"); // Button for applying the changes
		this.applyButton.setMaxWidth(Double.MAX_VALUE);
		this.optionsPane.getChildren().addAll(new Label(""), applyButton);

		this.table = new TableView(); // TableView for displaying data
		this.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // restricts size of
																				// table
		TableColumn firstCol = new TableColumn<>("Farm ID"); // three columns
		firstCol.setCellValueFactory(new PropertyValueFactory<>("id")); // specifies a cell factory
		TableColumn secondCol = new TableColumn<>("Total Wt.");
		secondCol.setCellValueFactory(new PropertyValueFactory<>("weight")); // specifies a cell factory
		TableColumn thirdCol = new TableColumn<>("% Total");
		thirdCol.setCellValueFactory(new PropertyValueFactory<>("percent")); // specifies a cell factory
		this.table.getColumns().add(firstCol);
		this.table.getColumns().add(secondCol);
		this.table.getColumns().add(thirdCol);

		VBox vbox = new VBox(); // VBox to organize the GridPane
		vbox.getChildren().add(table);
		this.reportPane.add(vbox, 0, 0); // adds vbox to left part of reportPane

		this.piechart = new PieChart(); // pie chart for displaying percentage of total weight by farm
		piechart.setTitle("Percentage of Total Weight");
		piechart.setMaxWidth(325);
		this.reportPane.add(piechart, 1, 0); // adds chart to right part of reportPane
		this.reportPane.getStyleClass().add("reportPane"); // styling

		applyButton.setOnAction(e -> apply()); // apply changes when button clicked

		// Deal with default input values
		updateValidMonths();

		if (!applyButton.isDisabled())
			apply();
	}

	/**
	 * Updates the list of valid months depending on which year has been selected
	 */
	private void updateValidMonths() {
		this.monthSelect.getItems().clear();
		this.monthSelect.getItems().addAll(data.getValidMonthsInYear(Integer.parseInt(this.yearSelect.getValue())));

		// Select the first valid month
		this.monthSelect.getSelectionModel().selectFirst();
		monthSelectionChanged();
	}

	/**
	 * Updates applyButton disabled status based on whether or not valid input has
	 * been entered
	 */
	private void monthSelectionChanged() {
		applyButton.setDisable(yearSelect.getValue() == null || monthSelect.getValue() == null);
	}

	/**
	 * Helper method to apply the changes to show in table and piechart
	 */
	public void apply() {
		String year = this.yearSelect.getValue(); // saves year selected
		int month; // saves month selected
		// converts month in String to int
		switch (String.valueOf(this.monthSelect.getValue())) {
		case "January":
			month = 0;
			break;
		case "February":
			month = 1;
			break;
		case "March":
			month = 2;
			break;
		case "April":
			month = 3;
			break;
		case "May":
			month = 4;
			break;
		case "June":
			month = 5;
			break;
		case "July":
			month = 6;
			break;
		case "August":
			month = 7;
			break;
		case "September":
			month = 8;
			break;
		case "October":
			month = 9;
			break;
		case "Novemebr":
			month = 10;
			break;
		case "December":
			month = 11;
			break;
		default:
			month = 0;
		}
		// gets all entries for specified year and month
		List<MilkEntry> entries = data.getEntriesForMonth(Integer.parseInt(year), month);
		Hashtable<String, Integer> weightsByID = new Hashtable<>(); // stores milk weight information by
																	// farm ID
		double totalWeight = 0; // tracks total weight
		// stores info and computes weight for every entry for the specified year and
		// month
		for (MilkEntry entry : entries) {
			int previous = 0;
			String id = entry.getFarmID();
			if (weightsByID.containsKey(id)) { // add the previous value if it exists
				previous = weightsByID.get(id);
			}
			weightsByID.put(id, entry.getWeight() + previous);
			totalWeight += entry.getWeight(); // adds up total weight of the specified year and month
		}

		ArrayList<Entry> tableData = new ArrayList<>(); // stores data to be used in table
		ArrayList<PieChart.Data> percentageData = new ArrayList<>(); // stores data to be used in
																		// piechart

		// adds data of weights and percentage of total weight for each individual farm
		for (String id : data.getFarmIDs()) {
			// checks if ID is valid
			if (!weightsByID.containsKey(id)) {
				continue;
			}
			double thisWeight = weightsByID.get(id).doubleValue();
			tableData.add(new Entry(id, weightsByID.get(id), (thisWeight / totalWeight) * 100)); // adds to table
			percentageData.add(new PieChart.Data(id, thisWeight)); // adds to piechart
		}
		this.table.setItems(FXCollections.observableArrayList(tableData)); // adds data into table
		this.piechart.setData(FXCollections.observableArrayList(percentageData)); // adds data into
																					// piechart
	}

	/**
	 * Generates the text used to write to output file
	 * 
	 * @return String used to write to output file
	 */
	@Override
	public String getTextReport() {
		// column labels
		StringBuilder report = new StringBuilder("Farm ID,Total Weight,Percent of Total Weight\n");
		// loops through every row in table and adds to report
		for (Entry entry : table.getItems()) {
			report.append(entry.id.getValue()).append(",");
			report.append(entry.weight.getValue()).append(",");
			report.append(entry.percent.getValue()).append("\n");
		}
		return report.toString();
	}
}
