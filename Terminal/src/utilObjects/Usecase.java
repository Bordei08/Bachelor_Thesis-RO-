package utilObjects;


import com.sun.javacard.apduio.Apdu;

public class Usecase {
	public static DateProvider currentDate;
	public static Employee employee;
	public static int id;
	public static Apdu result;

	public static String createCommandForCreateApplet() {
		String command = "";
		command += "0x80 0xB8 0x00 0x00 0x2A 0x0f 0x0A 0x00 0x00 0x00 0x00 0x0A 0x00 0x02 0x03 0x04 0x05 0x06 0x07 0x08 0x01";
		command += " 0x80";
		char[] pin = employee.getPin().toCharArray();
		Integer sizePin = pin.length;
		if (sizePin < 8) {
			Integer diff = 8 - (sizePin + 1);
			for (int i = 0; i < diff; i++) {
				command += " 0x00";
			}
			command += " " + convertIntegerToStringFromatByte(sizePin);
			for (int i = 0; i < sizePin; i++) {
				command += " 0x0" + pin[i];
			}
		} else {
			for (int i = 0; i < sizePin; i++) {
				command += " 0x0" + pin[i];
			}
		}
		command += " 0x01";
		command += " " + convertIntegerToStringFromatByte(id);
		command += " 0x08";
		command += " 0x0" + ((employee.getAreasAcces()[0] == true) ? "1" : "0");
		command += " 0x0" + ((employee.getAreasAcces()[1] == true) ? "1" : "0");
		command += " 0x0" + ((employee.getAreasAcces()[2] == true) ? "1" : "0");
		command += " 0x0" + ((employee.getAreasAcces()[3] == true) ? "1" : "0");
		command += " 0x0" + ((employee.getAreasAcces()[4] == true) ? "1" : "0");
		command += " 0x0" + ((employee.getAreasAcces()[5] == true) ? "1" : "0");
		command += " 0x0" + ((employee.getAreasAcces()[6] == true) ? "1" : "0");
		command += " 0x0" + ((employee.getAreasAcces()[7] == true) ? "1" : "0");

		command += " 0x02";
		command += " " + convertIntegerToStringFromatByte(employee.getHoursWorkedToday()) + " "
				+ convertIntegerToStringFromatByte(employee.getMinutesWorkedToday());

		command += " 0x02";
		command += " " + convertIntegerToStringFromatByte(employee.getHoursWorkedCurrentMonth()) + " 0x00";

		command += " 0x7f";
		return command;
	}// end of createCommandForCreateApplet method

	public static String createCommandForCheckPin(String pin) {
		String command = "0x80 0x20 0x00 0x00";
		char[] pinString = pin.toCharArray();
		Integer sizePin = pinString.length;
		command += " " + convertIntegerToStringFromatByte(sizePin);
		for (int i = 0; i < sizePin; i++) {
			command += " 0x0" + pinString[i];
		}
		command += " 0x7F";
		return command;
	}

	public static String convertIntegerToStringFromatByte(int number) {
		String result;
		byte byteValue = (byte) number;
		result = String.format("0x%02X", byteValue & 0xFF);
		return result;
	}// end of convertIntegerToStringFromatByte method

	public static boolean isExecuted(Apdu command) {
		byte[] sw1sw2 = command.getSw1Sw2();
		if (sw1sw2[0] == (byte) 0x90 && sw1sw2[1] == (byte) 0x00) {
			return true;
		}
		return false;
	}// end of isExecuted method

	public static String createCommandForCheckLastDay() {
		String command = "";
		command += "0x80 0x21 0x00 0x00 0x03";
		String dateString = currentDate.getDate();
		String year = dateString.substring(2, 4);
		String month = dateString.substring(5, 7);
		String day = dateString.substring(8, 10);
		command += " " + convertIntegerToStringFromatByte(Integer.parseInt(day));
		command += " " + convertIntegerToStringFromatByte(Integer.parseInt(month));
		command += " " + convertIntegerToStringFromatByte(Integer.parseInt(year));
		command += " 0x01";
		return command;
	}// end of createCommandForCheckLastDay method

	public static String createCommandForEntryInArea(Integer area) {
		String command = "0x80 0x30 0x00 0x00 0x01";
		command += " " + convertIntegerToStringFromatByte(area);
		command += " 0x7f";
		return command;
	}

	public static String createCommandForChangePin(String pin) {
		String command = "0x80 0x80 0x00 0x00";
		char[] pinString = pin.toCharArray();
		Integer sizePin = pinString.length;
		command += " " + convertIntegerToStringFromatByte(sizePin);
		for (int i = 0; i < sizePin; i++) {
			command += " 0x0" + pinString[i];
		}
		command += " 0x7F";
		return command;
	}

}
