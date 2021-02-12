/**
 * MilkWeight final project for A team 128
 * Project members: Joshua Faessler, Daniel Kouchekina, Ryan Sun, and Thiago Braga
 */

package application.gui.reports;

import application.data.MilkData;
import application.data.MilkEntry;
import application.gui.DashboardStage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Displays the custom date range report.
 *
 * @author Joshua Faessler (faessler, 2020)
 */
public class CustomDateRangeReportPane extends ReportPane {
	private final DatePicker startPicker;
	private final DatePicker endPicker;
	private final Label invalidLabel;
	private final Button applyButton;
	private final TableView<DisplayEntry> dataTable;
	private final PieChart pieChart;

	private MilkData data;

	/**
	 * Entries in the data table for TableView
	 */
	public class DisplayEntry { // needs to be public for access by javafx
		private final SimpleStringProperty farm;
		private final SimpleIntegerProperty weight;
		private final SimpleDoubleProperty percentage;

		DisplayEntry(String farm, int totalWeight, double percentage) {
			this.farm = new SimpleStringProperty(farm);
			this.weight = new SimpleIntegerProperty(totalWeight);

			// Round percentage
			percentage = Math.round(percentage * 1000) / 1000.0d;
			
			this.percentage = new SimpleDoubleProperty(percentage);
		}

		// Methods required by TableView
		public SimpleStringProperty farmProperty() {
			return farm;
		}

		public SimpleIntegerProperty weightProperty() {
			return weight;
		}

		public SimpleDoubleProperty percentageProperty() {
			return percentage;
		}
	}

	/**
	 * Constructor that builds GUI
	 *
	 * @param data  MilkData from loaded file(s)
	 * @param stage The current stage
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CustomDateRangeReportPane(MilkData data, DashboardStage stage) {
		super(data, stage);
		this.data = data;

		List<Integer> validYears = data.getValidYears();

		// Initialize components with a range from the beginning of the first year to
		// the end of the last year
		startPicker = new DatePicker(LocalDate.of(validYears.get(0), 1, 1));
		startPicker.setMaxWidth(Double.MAX_VALUE);
		endPicker = new DatePicker(LocalDate.of(validYears.get(validYears.size() - 1), 12, 31));
		endPicker.setMaxWidth(Double.MAX_VALUE);
		invalidLabel = new Label("Invalid Range!");
		applyButton = new Button("Apply");
		applyButton.setMaxWidth(Double.MAX_VALUE);
		validRange(); // Run valid range once with default values to set invalidLabel and applyButton

		// Build data table
		dataTable = new TableView<>();
		// Make columns fill space
		dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		// Create and name columns
		TableColumn<DisplayEntry, String> farmColumn = new TableColumn<>("Farm");
		TableColumn<DisplayEntry, String> weightColumn = new TableColumn<>("Total Wt.");
		TableColumn<DisplayEntry, String> percentColumn = new TableColumn<>("% Total");
		// Set columns to their data
		farmColumn.setCellValueFactory(new PropertyValueFactory("farm"));
		weightColumn.setCellValueFactory(new PropertyValueFactory("weight"));
		percentColumn.setCellValueFactory(new PropertyValueFactory("percentage"));
		dataTable.getColumns().setAll(farmColumn, weightColumn, percentColumn);

		// Build pie chart
		pieChart = new PieChart();
		pieChart.setTitle("Total Weights of All Farms");
		pieChart.setMaxWidth(325);

		// Add all components
		optionsPane.getChildren().add(new Label("Start Date"));
		optionsPane.getChildren().add(startPicker);
		optionsPane.getChildren().add(new Label("End Date"));
		optionsPane.getChildren().add(endPicker);
		optionsPane.getChildren().add(invalidLabel);
		optionsPane.getChildren().add(applyButton);

		reportPane.add(dataTable, 0, 1);
		reportPane.add(pieChart, 1, 1);
		dataTable.setVisible(false);
		pieChart.setVisible(false);

		// Set up event listeners
		startPicker.setOnAction(e -> validRange());
		endPicker.setOnAction(e -> validRange());
		applyButton.setOnAction(e -> applyRange());

		// Show data associated with default values
		applyRange();
	}

	/**
	 * Updates the options so that the "apply" button can only be pressed when a
	 * valid date range is selected. If the date is invalid the invalid date label
	 * will display.
	 */
	private void validRange() {
		if (!(startPicker.getValue() == null || endPicker.getValue() == null)
				&& startPicker.getValue().isBefore(endPicker.getValue())) {
			applyButton.setDisable(false); // enable apply button
			invalidLabel.setVisible(false); // hide invalid label
		} else { // date is invalid
			applyButton.setDisable(true);
			invalidLabel.setVisible(true);
		}

	}

	/**
	 * Displays the data according to the entered date range
	 */
	private void applyRange() {
		// Uses java.sql.Date to convert java.time.localDate to java.util.Date
		List<MilkEntry> entries = data.getEntriesForDateRange(java.sql.Date.valueOf(startPicker.getValue()),
				java.sql.Date.valueOf(endPicker.getValue()));

		// Get list of weights, as well as total production
		Hashtable<String, Integer> farmWeights = new Hashtable<>();
		double total = 0;
		for (MilkEntry entry : entries) {
			int previous = 0;
			String id = entry.getFarmID();
			if (farmWeights.containsKey(id)) // add the previous value if it exists
				previous = farmWeights.get(id);
			farmWeights.put(id, entry.getWeight() + previous);
			total += entry.getWeight();
		}

		ArrayList<DisplayEntry> tableData = new ArrayList<>();
		ArrayList<PieChart.Data> percentageData = new ArrayList<>();

		for (String id : data.getFarmIDs()) {
			// Only add if farm id is in the date range
			if (!farmWeights.containsKey(id))
				continue;
			double weight = farmWeights.get(id).doubleValue();
			tableData.add(new DisplayEntry(id, farmWeights.get(id), (weight / total) * 100));
			percentageData.add(new PieChart.Data(id, weight));
		}

		// set data table
		dataTable.setVisible(true);
		dataTable.setItems(FXCollections.observableArrayList(tableData));

		// set pie chart
		pieChart.setData(FXCollections.observableArrayList(percentageData));
		pieChart.setVisible(true); // Some sort of rendering issue if setVisible is earlier in this method
	}

	/**
	 * @return a string used to generate a text report of the processed data
	 */
	@Override
	public String getTextReport() {
		StringBuilder report = new StringBuilder("Farm ID,Total Weight,Percent of Total Weight\n");
		for (DisplayEntry entry : dataTable.getItems()) {
			report.append(entry.farm.getValue()).append(",");
			report.append(entry.weight.getValue()).append(",");
			report.append(entry.percentage.getValue()).append("\n");
		}
		return report.toString();
	}

}
