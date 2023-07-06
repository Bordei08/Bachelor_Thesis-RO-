package utilObjects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateProvider {

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private String date;

	public DateProvider() {
		LocalDate currentDate = LocalDate.now();
		this.date = currentDate.format(dateFormatter);
	}

	public void setCustomDate(int year, int month, int day) {
		LocalDate customDate = LocalDate.of(year, month, day);
		this.date = customDate.format(dateFormatter);
	}

	public String getDate() {
		return this.date;
	}

	public Integer getCurrentMonth() {
		LocalDate var = LocalDate.parse(this.date, DateTimeFormatter.ISO_DATE);
		int month = var.getMonthValue();
		return month;
	}

}
