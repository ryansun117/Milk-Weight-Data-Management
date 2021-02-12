/**
 * MilkWeight final project for A team 128
 * Project members: Joshua Faessler, Daniel Kouchekina, Ryan Sun, and Thiago Braga
 */


package application.data;

import java.util.Date;

/**
 * Represents a single row of the milk data
 * 
 * @author Daniel Kouchekinia
 *
 */
public class MilkEntry implements Comparable<MilkEntry> {

	private String farmID;
	private Date date;
	private int weight;

	/**
	 * Initializes a new MilkEntry with the specified information
	 * 
	 * @param farmID The entry's farm ID
	 * @param date   The entry's date
	 * @param weight The entry's weight
	 */
	public MilkEntry(String farmID, Date date, int weight) {
		this.farmID = farmID;
		this.date = date;
		this.weight = weight;
	}

	/**
	 * Returns the entry's farm ID
	 * 
	 * @return the entry's farm ID
	 */
	public String getFarmID() {
		return farmID;
	}

	/**
	 * Returns the entry's date
	 * 
	 * @return the entry's date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Returns the entry's weight
	 * 
	 * @return the entry's weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * Generates a string representing the entry. Eg: "Farm: Farm 30, Weight: 11640,
	 * Date: Thu Jan 17 00:00:00 PST 2019"
	 */
	@Override
	public String toString() {
		return String.format("Farm: %s, Weight: %d, Date: %s\n", farmID, weight, date.toString());
	}

	/**
	 * Compares this MilkEntry against another based on their dates
	 */
	@Override
	public int compareTo(MilkEntry o) {
		return this.date.compareTo(o.date);
	}
}
