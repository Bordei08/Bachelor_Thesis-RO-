package utilObjects;

import dataBase.*;

public class Employee {
	private String name;
	private Integer id;
	private Integer hoursWorkedToday;
	private Integer minutesWorkedToday;
	private Integer hoursWorkedCurrentMonth;
	private boolean[] areasAccess;
	private String pin;
	private DatabaseUtil db;

	public Employee(Integer id, DateProvider date) {
		this.areasAccess = new boolean[8];
		this.db = new DatabaseUtil(date);
		this.id = id;
		this.name = db.getNameFromDB(id);

		if (!db.isDateAlreadyExists(id)) {
			db.insertNewDate(id);
		}

		Integer[] resultTime = new Integer[2];
		resultTime = db.getHoursWorkedTodayFromDB(id);

		this.hoursWorkedToday = resultTime[0];
		this.minutesWorkedToday = resultTime[1];
		this.hoursWorkedCurrentMonth = db.getHoursWorkedCurrentMonthFromDB(id);
		boolean[] result = new boolean[8];
		result = db.getAreasAccesFromDB(id);
		System.arraycopy(result, 0, this.areasAccess, 0, result.length);
		this.pin = db.getPinFromDB(id);
		// System.out.println(db.isDateAlreadyExists(id)); testing

	}

	public String getName() {
		return this.name;
	}

	public Integer getHoursWorkedToday() {
		return this.hoursWorkedToday;
	}

	public Integer getHoursWorkedCurrentMonth() {
		return this.hoursWorkedCurrentMonth;
	}

	public boolean[] getAreasAcces() {
		return this.areasAccess;
	}

	public String getPin() {
		return this.pin;
	}

	public Integer getId() {
		return this.id;
	}

	public DatabaseUtil getDB() {
		return this.db;
	}

	public Integer getMinutesWorkedToday() {
		return this.minutesWorkedToday;
	}

}
