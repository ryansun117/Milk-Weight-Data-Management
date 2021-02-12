/**
 * MilkWeight final project for A team 128
 * Project members: Joshua Faessler, Daniel Kouchekina, Ryan Sun, and Thiago Braga
 */


//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title: Milk Weights
// Files: Main.java, MilkData.java, AnnualReportPane.java, ReportPane.java, DashboardStage.java
//
// Deadline: April 30th
//
// Course: CS 400, spring 2020
// Lecture: 002
// Description: Final A-team project – Create program allowing users to visualize organized data
// from .csv files containing raw data points.
//
// Author: Thiago S. Braga
// Email: tbraga@wisc.edu
// Lecturer's Name: Debra Deppeler
//
///////////////////////////// ACKNOWLEDGMENTS /////////////////////////////////
//
// Students who get help from outside sources must fully
// acknowledge and credit those sources of help here. Instructors and TAs do
// not need to be credited here, but tutors, friends, relatives, room mates,
// strangers, and others do. If you received no outside help from either type
// of source, then please explicitly indicate NONE.
//
// Persons: A-Team 128
// Online Sources: None.
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////

package application.gui.reports;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import application.data.MilkData;
import application.gui.DashboardStage;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import application.data.*;

/**
 * A class to comprise the inputs and outputs of the annual report tab when selected.
 * 
 * @author Thiago S. Braga
 */
public class AnnualReportPane extends ReportPane {

  // Global data fields:
  private Integer selectedYear = null; // The user's selected year of data to consider.
  private List<FarmTotal> farmTotals = null; // A list of individual total weights per farm.
  private Double totalAnnualWeight = 0.0; // The sum of all farm weights in the selected year.

  // Interface elements:
  private ComboBox<Integer> yearInput; // User's selected year of analysis.
  private Button applyButton; // Confirms user's desired inputs.

  /**
   * An inner class representing the amalgamation of individual weights per farm ID in the user's
   * specified year.
   * 
   * @author Thiago S. Braga
   *
   */
  public class FarmTotal {

    private SimpleStringProperty farmID; // The identifying value of a farm.
    private SimpleDoubleProperty totalFarmWeight; // Sum of all individual weights for the farm.

    /**
     * Constructor for creating a FarmTotal object.
     * 
     * @param farmID          - an value identifying a specific farm.
     * @param totalFarmWeight - the sum of individual weights per farm in one specific year.
     */
    public FarmTotal(String farmID, Double totalFarmWeight) {
      this.farmID = new SimpleStringProperty(farmID);
      this.totalFarmWeight = new SimpleDoubleProperty(totalFarmWeight);
    }

    /**
     * Necessary getter method for farmID field.
     * 
     * @return farmID.
     */
    public String getFarmID() {
      return farmID.get();
    }

    /**
     * Setter method for farmID field.
     * 
     * @param newID - the new ID to be set.
     */
    public void setFarmID(String newID) {
      farmID.set(newID);
    }

    /**
     * Necessary getter method for totalFarmWeight field.
     * 
     * @return totalFarmWeight.
     */
    public Double getTotalFarmWeight() {
      return totalFarmWeight.get();
    }

    /**
     * Setter method for totalFarmWeight.
     * 
     * @param newTotalWeight - the new weight to be set.
     */
    public void setTotalFarmWeight(Double newTotalWeight) {
      totalFarmWeight.set(newTotalWeight);
    }
  }

  /**
   * Constructor for the annual report pane.
   * 
   * Responsible for establishing the interface of the pane and handling user input.
   * 
   * @param data  - Object containing user's provided raw data.
   * @param stage - Stage to display interface.
   */
  public AnnualReportPane(MilkData data, DashboardStage stage) {

    super(data, stage); // Call ReportPane constructor.

    // Construct left-hand menu pane.
    yearInput = new ComboBox<Integer>(FXCollections.observableArrayList(data.getValidYears()));
    // Select the first item by default
    yearInput.getSelectionModel().selectFirst();
    yearInput.setMaxWidth(Double.MAX_VALUE);
    yearInput.setOnAction((e) -> inputsUpdated());
    
    applyButton = new Button("Apply");
    applyButton.setMaxWidth(Double.MAX_VALUE);
    optionsPane.getChildren().addAll(new Label("Year:"), yearInput, new Label(""), applyButton);

    // Create event handling for apply button click.
    applyButton.setOnAction(e -> updateResults(data));
    
    // Update from initial settings
    inputsUpdated();
    
    if(!applyButton.isDisabled())
    	updateResults(data);
  }
  
  /**
   * Updates applyButton disabled status based on whether or not valid input has been entered
   */
  private void inputsUpdated () {
	  applyButton.setDisable(yearInput.getValue() == null);
  }

  /**
   * Method responsible for reading user's selected year, fetching and manipulating data, and
   * displaying graphical results to interface with assistance from other helper methods.
   * 
   * @param data  - unprocessed raw data.
   */
  private void updateResults(MilkData data) {

    selectedYear = yearInput.getValue(); // Obtain user's selection.

    // Fetch individual data parameters from raw data.
    List<String> farmIDList = data.getFarmIDs();
    List<MilkEntry> annualData = data.getEntriesForYear(selectedYear);

    // Create FarmTotal objects to represent total weights per farm.
    farmTotals = createFarmTotals(farmIDList, annualData);

    // Calculate total overall weight from individual farm weights.
    for (FarmTotal farm : farmTotals)
      totalAnnualWeight += farm.getTotalFarmWeight();

    createTable(); // Build and display table of individual farm totals.

    // Format pie chart data and display pie chart using FarmTotal objects.
    ObservableList<PieChart.Data> pieChartData = getPieChartData(); 
    displayPercentOfTotal(pieChartData);
  }

  /**
   * Calculates the total weight produced per farm in a given year from a list of individual farm
   * IDs containing no duplicates and the parsed entries from data file.
   * 
   * @param farmIDList - a list of farm IDs containing no duplicates
   * @param entries    - individual entries parsed from user's data file.
   * @return - a list of FarmTotal objects.
   */
  private List<FarmTotal> createFarmTotals(List<String> farmIDList, List<MilkEntry> entries) {

    // Create an array to store total weights during calculation.
    double[] totalWeightArray = new double[farmIDList.size()];

    // The indices of the totalWeightArray will correspond with the indices of individual
    // farm IDs in the farmIDList.
    // For each entry, add the weight to its appropriate index.
    for (int i = 0; i < entries.size(); i++)
      totalWeightArray[farmIDList.indexOf(entries.get(i).getFarmID())] +=
          entries.get(i).getWeight();

    farmTotals = new ArrayList<FarmTotal>(); // Initialize the farmTotals list.

    // Create FarmTotal objects given a farm ID and total weight.
    for (int i = 0; i < farmIDList.size(); i++)
      farmTotals.add(new FarmTotal(farmIDList.get(i), totalWeightArray[i]));

    return farmTotals;
  }

  /**
   * Helper method for creating and displaying table of farm weight totals.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void createTable() {

    ObservableList<FarmTotal> tableData = FXCollections.observableArrayList();
    tableData.addAll(farmTotals);

    // Create the farm total table.
    TableView table = new TableView();

    // Ensure columns fill entire table
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    // Establish identifier column.
    TableColumn farmIDColumn = new TableColumn("Farm ID");
    farmIDColumn.setCellValueFactory(new PropertyValueFactory<FarmTotal, String>("farmID"));

    // Establish data column.
    TableColumn totalFarmWeightColumn = new TableColumn("Total Weight");
    totalFarmWeightColumn
        .setCellValueFactory(new PropertyValueFactory<FarmTotal, String>("totalFarmWeight"));

    table.setItems(tableData); // Load the data.
    table.getColumns().setAll(farmIDColumn, totalFarmWeightColumn); // Add columns to table.

    reportPane.add(table, 0, 0);
  }

  /**
   * Helper method for creating and displaying pie chart of weight percentages.
   * 
   * @param pieChartData - percent of total data ready to be displayed.
   */
  private void displayPercentOfTotal(ObservableList<PieChart.Data> pieChartData) {

    // Create pie chart object.
    PieChart pieChart = new PieChart(pieChartData);
    pieChart.setTitle("Percent of Total Weight");
    pieChart.setMaxWidth(325);

    // Add pie chart to second column, first row of data display grid.
    reportPane.add(pieChart, 1, 0);
  }

  /**
   * Helper method for processing raw milk data for a pie chart.
   * 
   * @return data ready to be displayed in pie chart.
   */
  private ObservableList<PieChart.Data> getPieChartData() {

    // Create data collection object.
    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

    for (int i = 0; i < farmTotals.size(); i++)
      pieChartData.add(new PieChart.Data(farmTotals.get(i).getFarmID(),
          getPercentOfTotal(farmTotals.get(i).getTotalFarmWeight())));

    return pieChartData;
  }

  /**
   * A private helper method that calculates the percentage of a given year's total annual weight
   * attributable to an individual farm.
   * 
   * @param farmWeight - the individual farm's total annual weight.
   * @return - the percent of total weight attributable to the individual farm.
   */
  private Double getPercentOfTotal(Double farmWeight) {
    return (farmWeight / totalAnnualWeight) * 100;
  }

  /**
   * Creates an contiguous String to be printed to .csv file.
   * 
   * Format: farmID,totalFarmWeight,percentOfTotal
   */
  @Override
  public String getTextReport() {

    // Conventionally reported percent format.
    DecimalFormat hundredths = new DecimalFormat("###.##");

    StringBuilder report = new StringBuilder("Farm ID,Total Weight,Percent of Total Weight\n");
    for (FarmTotal entry : farmTotals) {
      report.append(entry.getFarmID()).append(",");
      report.append(entry.getTotalFarmWeight()).append(",");
      report.append(hundredths.format(getPercentOfTotal(entry.getTotalFarmWeight().doubleValue())))
          .append("\n");
    }
    return report.toString();
  }
}
