/**
 * MilkWeight final project for A team 128
 * Project members: Joshua Faessler, Daniel Kouchekina, Ryan Sun, and Thiago Braga
 */


package application.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Data structure to store and retrieve MilkEntries
 * @author Daniel Kouchekinia
 *
 */
public class MilkData {

	// All months in the year
	private final String MONTHS[] = { "January", "February", "March", "April", "May", "June", "July", "August",
			"September", "October", "November", "December" };

	// Required for creating new arrays of ArrayList<MilkEntry>
	private final Class<?> MILK_ENTRY_LIST_CLASS = new ArrayList<MilkEntry>().getClass();

	// Information for GUI input drop-downs
	private List<String> farmIDs;
	private TreeMap<Integer, List<Integer>> validMonthsInYear;

	// Maps years to an array of 12 (one for each month) lists of MilkEntries
	private HashMap<Integer, List<MilkEntry>[]> monthEntryHierarchy;

	// Maps farm IDs to lists of MilkEntrys
	private HashMap<String, List<MilkEntry>> farmLists;

	// Calendar used to retrieve year and month information from dates
	private final Calendar calendar;

	/**
	 * Initializes a new empty instance of the data structure.
	 */
	public MilkData() {
		// Initialize data structures used to hold GUI drop-down data
		this.farmIDs = new ArrayList<String>();
		this.validMonthsInYear = new TreeMap<Integer, List<Integer>>();

		// Initialize main data structures where MilkEntries are held
		// Note that this storage is redundant for the sake of access speed
		this.monthEntryHierarchy = new HashMap<Integer, List<MilkEntry>[]>(50);
		this.farmLists = new HashMap<String, List<MilkEntry>>(250);

		// Initialize the calendar used for gathering month/year info as a Gregorian
		// calendar
		this.calendar = new GregorianCalendar();
	}

	/**
	 * Adds a single MilkEntry to the internal data structures.
	 * 
	 * @param entry The MilkEntry to add to the internal data structures.
	 */
	@SuppressWarnings("unchecked")
	public void addEntry(MilkEntry entry) {
		// Get the year and month from the entry
		calendar.setTime(entry.getDate());

		Integer year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);

		// ** UPDATE GUI INPUT INFORMATION **

		// Update the farm ID list
		if (!farmIDs.contains(entry.getFarmID()))
			farmIDs.add(entry.getFarmID());

		// Update validMonthsInYear
		List<Integer> validMonths = validMonthsInYear.get(year);

		// Create the list of valid months for the specific year if it does not already
		// exist
		if (validMonths == null) {
			validMonths = new ArrayList<Integer>();
			validMonthsInYear.put(year, validMonths);
		}

		if (!validMonths.contains(month))
			validMonths.add(month);

		// ** UPDATE MAIN DATA STRUCTURES **

		// Update the monthEntryHierarchy
		List<MilkEntry>[] monthsLists = monthEntryHierarchy.get(year);

		// Create the array of 12 lists of MilkEntries for the specific year if it does
		// not already exist
		if (monthsLists == null) {
			monthsLists = (List<MilkEntry>[]) Array.newInstance(MILK_ENTRY_LIST_CLASS, 12);
			monthEntryHierarchy.put(year, monthsLists);
		}

		List<MilkEntry> listForMonth = monthsLists[month];

		// Create the list for the specific month if it does not already list
		if (listForMonth == null)
			listForMonth = (monthsLists[month] = new ArrayList<MilkEntry>());

		listForMonth.add(entry);

		// Update farmLists
		List<MilkEntry> listForFarm = farmLists.get(entry.getFarmID());

		// Create the list for the specific farm if it does not already exist
		if (listForFarm == null) {
			listForFarm = new ArrayList<MilkEntry>();
			farmLists.put(entry.getFarmID(), listForFarm);
		}

		listForFarm.add(entry);
	}

	/**
	 * Sorts all internal data structures. Note that this should only be performed
	 * once, after all data has been inputed.
	 */
	public void organize() {
		// Sort the farmIDs by alphanumeric order
		Collections.sort(farmIDs);

		// Organize valid months
		validMonthsInYear.forEach((Integer year, List<Integer> list) -> {
			if (list == null)
				return;
			Collections.sort(list);
		});

		// Organize the month entry hierarchy
		monthEntryHierarchy.forEach((Integer year, List<MilkEntry>[] monthList) -> {
			for (int month = 0; month < 12; month++) {
				if (monthList[month] == null)
					continue;
				Collections.sort(monthList[month]);
			}
		});

		// Organize the farm lists
		farmLists.forEach((String farmID, List<MilkEntry> list) -> {
			if (list == null)
				return;
			Collections.sort(list);
		});
	}

	/**
	 * Returns a list of all farm IDs. Note that this list will be sorted if
	 * {@link #organize()} was called after the last data member was entered
	 * 
	 * @return a list of all farm IDs
	 */
	public List<String> getFarmIDs() {
		return farmIDs;
	}

	/**
	 * Returns a list of all years for which there are entries
	 * 
	 * @return a list of all years for which there are entries
	 */
	public List<Integer> getValidYears() {
		return new ArrayList<Integer>(validMonthsInYear.keySet());
	}

	/**
	 * Returns a list of all months in the year specified for which there are
	 * entries. Note that this list will be sorted if {@link #organize()} was called
	 * after the last data member was entered
	 * 
	 * @param year The year to search for valid months in.
	 * @return a list of all months in the year specified for which there are
	 *         entries
	 */
	public List<String> getValidMonthsInYear(int year) {
		List<String> monthsList = new ArrayList<String>();
		// Loop through all valid months in the year specified
		validMonthsInYear.getOrDefault(year, Collections.<Integer>emptyList()).forEach((Integer monthIndex) -> {
			// Add the string version of the month to the list
			monthsList.add(MONTHS[monthIndex]);
		});
		return monthsList;
	}

	/**
	 * Returns a list of all Entries for the specified month in the specified year
	 * Note that this list will be sorted if {@link #organize()} was called after
	 * the last data member was entered
	 * 
	 * @param year  The year to look for entries in
	 * @param month The month to look for entries in
	 * @return A list of all Entries for the specified month in the specified year
	 * @throws IllegalArgumentException if the specified month is not 0-11
	 */
	public List<MilkEntry> getEntriesForMonth(int year, int month) throws IllegalArgumentException {
		// Ensure the month is 0-11
		if (month < 0 || month >= 12)
			throw new IllegalArgumentException("Month should be a number between 0 and 11.");

		// Get the list of MilkEntries for each month
		List<MilkEntry>[] months = monthEntryHierarchy.get(year);

		// Return an empty list if no entry has been added for the given year or the
		// given month in the given year
		if (months == null || months[month] == null)
			return Collections.<MilkEntry>emptyList();

		return months[month];
	}

	/**
	 * Returns a list of all Entries for the specified month in the specified year
	 * for the specified farm Note that this list will be sorted if
	 * {@link #organize()} was called after the last data member was entered
	 * 
	 * @param year   The year to look for entries in
	 * @param month  The month to look for entries in
	 * @param farmID The farm to look for entries for
	 * @return a list of all Entries for the specified month in the specified year
	 *         for the specified farm
	 * @throws IllegalArgumentException if the specified month is not 0-11
	 */
	public List<MilkEntry> getEntriesForMonth(int year, int month, String farmID) throws IllegalArgumentException {
		List<MilkEntry> entriesForFarm = new ArrayList<MilkEntry>();

		// Get the entries for the specified month
		List<MilkEntry> entriesForMonth = getEntriesForMonth(year, month);

		// Loop through each entry, ensuring that the farmID equals the farmID specified
		for (MilkEntry entry : entriesForMonth)
			if (entry.getFarmID().equals(farmID))
				entriesForFarm.add(entry);

		return entriesForFarm;
	}

	/**
	 * Returns a list of all entries for a specified year Note that this list will
	 * be sorted if {@link #organize()} was called after the last data member was
	 * entered
	 * 
	 * @param year The year to look for entries in
	 * @return a list of all entries for the specified year
	 */
	public List<MilkEntry> getEntriesForYear(int year) {
		List<MilkEntry> yearList = new ArrayList<MilkEntry>();

		// Get the array of MilkEntry lists for each month of the year specified
		List<MilkEntry>[] monthsLists = monthEntryHierarchy.get(year);

		// Loop through each month adding everything to the year list
		for (int month = 0; month < 12; month++)
			if (monthsLists[month] != null)
				yearList.addAll(monthsLists[month]);

		return yearList;
	}

	/**
	 * Returns a list of all entries for a specified farm Note that this list will
	 * be sorted if {@link #organize()} was called after the last data member was
	 * entered
	 * 
	 * @param farmID The farm to search for entries about
	 * @return a list of all entries for the specified farm
	 */
	public List<MilkEntry> getEntriesForFarm(String farmID) {
		return farmLists.getOrDefault(farmID, Collections.<MilkEntry>emptyList());
	}

	/**
	 * Returns a list of all entries between (inclusive) two specified dates Note
	 * that this list will be sorted if {@link #organize()} was called after the
	 * last data member was entered
	 * 
	 * @param startDate The beginning of the date range
	 * @param endDate   The end of the date range
	 * @return a list of all entries between the (inclusive) two specified dates
	 */
	public List<MilkEntry> getEntriesForDateRange(Date startDate, Date endDate) {
		List<MilkEntry> entries = new ArrayList<MilkEntry>();

		// Find the year and month of the first date
		calendar.setTime(startDate);
		int startYear = calendar.get(Calendar.YEAR);
		int startMonth = calendar.get(Calendar.MONTH);

		// Find the year and month of the second date
		calendar.setTime(endDate);
		int endYear = calendar.get(Calendar.YEAR);
		int endMonth = calendar.get(Calendar.MONTH);

		// Loop through all years between (inclusive) startDate and endDate
		for (int year = startYear; year <= endYear; year++)
			// Loop through all months between (inclusive) the start and end date
			for (int month = (year == startYear ? startMonth : 0); month <= (year == endYear ? endMonth : 11); month++)
				// Loop through all data entries for the year and month combination
				for (MilkEntry entry : getEntriesForMonth(year, month))
					// Only include the entry if it is between (inclusive) the start and end date
					if (entry.getDate().getTime() >= startDate.getTime()
							&& entry.getDate().getTime() <= endDate.getTime())
						entries.add(entry);

		return entries;
	}

}
