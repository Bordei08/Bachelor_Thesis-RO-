package com.card.applet;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.OwnerPIN;
import javacardx.framework.time.SysTime;
import javacardx.framework.time.TimeDuration;

public class BadgeEmployee extends Applet {

	/* constants declaration */

	// code of CLA byte in the command APDU header
	final static byte Badge_Employee_CLA = (byte) 0x80;

	// codes of INS byte in the command APDU header
	final static byte VERIFY = (byte) 0x20;
	final static byte ENTRY_IN_AREA = (byte) 0x30;
	final static byte EXIT_OF_AREA = (byte) 0x40;
	final static byte EXIT_FROM_COMPANY = (byte) 0x50;
	final static byte GET_INFORMATIONS = (byte) 0x70;
	final static byte CHANGE_PIN = (byte) 0x80;
	final static byte COMPLETE_WORK = (byte) 0x90;
	final static byte END_OF_THE_MONTH = (byte) 0x21;

	// maximum number of incorrect tries before the
	// PIN is blocked
	final static byte PIN_TRY_LIMIT = (byte) 0x03;
	// maximum size PIN
	final static byte MAX_PIN_SIZE = (byte) 0x08;
	// maxim number of rooms
	final static byte MAX_ROOMS_NUMBERS = (byte) 0x08;
	// 160
	final static byte PERFECTLY_WORKED = (byte) 0xa0;
	// 152
	final static byte MINIMUM_HOURS_WORKED = (byte) 0x98;

	// signal that the PIN verification failed
	final static short SW_VERIFICATION_FAILED = 0x6300;
	// signal that you do not have access to the room
	final static short SW_SECURITY_STATUS_NOT_SATISFIED = 0x6982;
	// signal that you have no way to leave the room (most likely because you are
	// not in that room)
	// you are already in another room
	// you are not in the company
	final static short SW_CONDITIONS_NOT_SATISFIED = (short) 0x6985;
	// For entry in company or other activity
	final static short SW_PIN_VERIFICATION_REQUIRED = (short) 0x6301;
	// wrong parameters
	final static short SW_WRONG_PARAMETERS = (short) 0x6B00;
	// Incorrect session data size
	final static short SW_INCORRECT_DATA_SIZE = (short) 0x9D07;
	// incorrect data
	final static short SW_WRONG_DATA = (short) 0x6A80;
	// the length of the received data is not as expected.
	final static short SW_WRONG_LENGTH = (short) 0x6700;

	/* instance variables declaration */
	OwnerPIN pin;
	byte[] acces_areas;
	byte current_area;
	byte in_company;
	byte id;
	short hours_worked_today;
	short minutes_worked_today;
	byte hours_worked_current_month;
	byte minutes_worked_current_month;
	TimeDuration start_time;
	TimeDuration end_time;

	private BadgeEmployee(byte[] bArray, short bOffset, byte bLength) {

		// It is good programming practice to allocate
		// all the memory that an applet needs during
		// its lifetime inside the constructor
		pin = new OwnerPIN(PIN_TRY_LIMIT, MAX_PIN_SIZE);

		byte aid_Len = bArray[bOffset]; // aid length
		bOffset = (short) (bOffset + aid_Len + 1);
		byte pin_Len = bArray[bOffset]; // pin length
		bOffset = (short) (bOffset + pin_Len + 1);
		byte id_Len = bArray[bOffset]; // id length

		// The installation parameters contain the PIN
		// initialization value
		pin.update(bArray, (short) (bOffset + 1), id_Len);

		bOffset = (short) (bOffset + id_Len + 1);
		byte id_size = bArray[bOffset];
		bOffset = (short) (bOffset + id_size);
		byte id_value = bArray[bOffset];
		// The ID set for the employee
		id = id_value;

		bOffset = (short) (bOffset + 1);
		byte area_number = bArray[bOffset];
		// We check if we receive 8 bytes that represent access to the 8 zones
		// If the "i" byte is 0x00 then we do not have access to the "i" area
		// Otherwise I would have
		if (area_number != MAX_ROOMS_NUMBERS) {
			ISOException.throwIt(SW_WRONG_LENGTH);
		}
		// Access set for areas
		acces_areas = new byte[8];
		bOffset = (short) (bOffset + 1);
		acces_areas[0] = bArray[bOffset];
		bOffset = (short) (bOffset + 1);
		acces_areas[1] = bArray[bOffset];
		bOffset = (short) (bOffset + 1);
		acces_areas[2] = bArray[bOffset];
		bOffset = (short) (bOffset + 1);
		acces_areas[3] = bArray[bOffset];
		bOffset = (short) (bOffset + 1);
		acces_areas[4] = bArray[bOffset];
		bOffset = (short) (bOffset + 1);
		acces_areas[5] = bArray[bOffset];
		bOffset = (short) (bOffset + 1);
		acces_areas[6] = bArray[bOffset];
		bOffset = (short) (bOffset + 1);
		acces_areas[7] = bArray[bOffset];

		bOffset = (short) (bOffset + 1);
		byte dim_t_today = bArray[bOffset];
		// We check that we get two bytes
		// one for the number of hours
		// one for the number of minutes
		// worked in the current day.
		if (dim_t_today != 0x02) {
			ISOException.throwIt(SW_WRONG_LENGTH);
		}

		bOffset = (short) (bOffset + 1);
		hours_worked_today = bArray[bOffset];
		bOffset = (short) (bOffset + 1);
		// We check if the number of minutes is less than 60
		if (bArray[bOffset] > 0x3c) {
			ISOException.throwIt(SW_WRONG_DATA);
		}
		minutes_worked_today = bArray[bOffset];

		bOffset = (short) (bOffset + 1);
		byte dim_t_month = bArray[bOffset];
		// We check that we get two bytes
		// one for the number of hours
		// one for the number of minutes
		// worked in the current month.
		if (dim_t_month != 0x02) {
			ISOException.throwIt(SW_WRONG_LENGTH);
		}

		bOffset = (short) (bOffset + 1);
		hours_worked_current_month = bArray[bOffset];
		;
		bOffset = (short) (bOffset + 1);
		// We check if the number of minutes is less than 60
		if (bArray[bOffset] > 0x3c) {
			ISOException.throwIt(SW_WRONG_DATA);
		}
		minutes_worked_current_month = bArray[bOffset];

		// I set in_company to 0x00 (= is not in company)
		in_company = 0x00;
		// I set current_area to 0x08 (= is not in any area )
		current_area = 0x08;

		register();
	} // end of the constructor

	public static void install(byte[] bArray, short bOffset, byte bLength) {
		// create a Badge applet instance
		new BadgeEmployee(bArray, bOffset, bLength);
	} // end of install method

	@Override
	public boolean select() {

		// The applet declines to be selected
		// if the pin is blocked.
		if (pin.getTriesRemaining() == 0) {
			return false;
		}

		return true;

	}// end of select method

	@Override
	public void deselect() {

		// reset the pin value
		pin.reset();

	}

	@Override
	public void process(APDU apdu) {

		// APDU object carries a byte array (buffer) to
		// transfer incoming and outgoing APDU header
		// and data bytes between card and CAD

		// At this point, only the first header bytes
		// [CLA, INS, P1, P2, P3] are available in
		// the APDU buffer.
		// The interface javacard.framework.ISO7816
		// declares constants to denote the offset of
		// these bytes in the APDU buffer

		byte[] buffer = apdu.getBuffer();
		// check SELECT APDU command

		if (apdu.isISOInterindustryCLA()) {
			if (buffer[ISO7816.OFFSET_INS] == (byte) (0xA4)) {
				return;
			}
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
		}

		// verify the reset of commands have the
		// correct CLA byte, which specifies the
		// command structure
		if (buffer[ISO7816.OFFSET_CLA] != Badge_Employee_CLA) {
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
		}

		switch (buffer[ISO7816.OFFSET_INS]) {

		case VERIFY:
			verify(apdu);
			return;
		case ENTRY_IN_AREA:
			entry_in_area(apdu);
			return;
		case EXIT_OF_AREA:
			exit_of_area(apdu);
			return;
		case EXIT_FROM_COMPANY:
			exit_from_company(apdu);
			return;
		case GET_INFORMATIONS:
			get_informations(apdu);
			return;
		case CHANGE_PIN:
			change_pin(apdu);
			return;
		case COMPLETE_WORK:
			check_complete_work(apdu);
			return;
		case END_OF_THE_MONTH:
			check_end_of_the_month(apdu);
			return;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}

	} // end of process method

	private void verify(APDU apdu) {

		byte[] buffer = apdu.getBuffer();
		// retrieve the PIN data for validation.
		byte byteRead = (byte) (apdu.setIncomingAndReceive());
		// check pin
		// the PIN data is read into the APDU buffer
		// at the offset ISO7816.OFFSET_CDATA
		// the PIN data length = byteRead
		if (pin.check(buffer, ISO7816.OFFSET_CDATA, byteRead) == false) {
			ISOException.throwIt(SW_VERIFICATION_FAILED);
		}

		// pre-allocate 1 duration instance
		if (in_company == (byte) 0x00) {
			start_time = TimeDuration.getInstance(JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT);
			// get timestamp
			SysTime.uptime(start_time);
		}
		in_company = 0x01;
	} // end of validate method

	public void change_pin(APDU apdu) {
		if (!pin.isValidated()) {
			ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		}
		byte[] buffer = apdu.getBuffer();
		// retrieve the PIN data for change.
		byte byteRead = (byte) (apdu.setIncomingAndReceive());

		byte numBytes = (buffer[ISO7816.OFFSET_LC]);

		if ((numBytes > MAX_PIN_SIZE) || (byteRead > MAX_PIN_SIZE) || numBytes < 4 || byteRead < 4) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		}

		pin.update(buffer, ISO7816.OFFSET_CDATA, byteRead);
	}// end of change_pin method

	public void get_informations(APDU apdu) {
		// access authentication
		if (!pin.isValidated()) {
			ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		}
		byte[] buffer = apdu.getBuffer();
		// inform system that the applet has finished
		// processing the command and the system should
		// now prepare to construct a response APDU
		// which contains data field
		short le = apdu.setOutgoing();
		// get balance in ron and liters
		// if p1 and p1 is 01 , we return 12 bytes , two for balance in ron , and
		// another two for balance in liters
		if (le < 6) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		} else {
			// informs the CAD the actual number of bytes
			// returned
			apdu.setOutgoingLength((byte) 6);

			// send id, current_area, hours&minutes worked today, hours&minutes worked
			// current month

			buffer[0] = (byte) (id);

			buffer[1] = (byte) (current_area);

			buffer[2] = (byte) (hours_worked_today);

			buffer[3] = (byte) (minutes_worked_today);

			buffer[4] = (byte) (hours_worked_current_month);

			buffer[5] = (byte) (minutes_worked_current_month);

			apdu.sendBytes((short) 0, (short) 6);

		}
	}// end of get_informations method

	public void entry_in_area(APDU apdu) {
		// access authentication
		if (!pin.isValidated()) {
			ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		}
		byte[] buffer = apdu.getBuffer();

		// Lc byte denotes the number of bytes in the
		// data field of the command APDU
		byte numBytes = buffer[ISO7816.OFFSET_LC];

		// indicate that this APDU has incoming data
		// and receive data starting from the offset
		// ISO7816.OFFSET_CDATA following the 5 header
		// bytes.
		byte byteRead = (byte) (apdu.setIncomingAndReceive());

		if (numBytes < 1 || byteRead < 1) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		}
		// get area
		// if not have acces for this area
		short area = (short) (buffer[ISO7816.OFFSET_CDATA] & 0xFF);

		if (area > 0x07) {
			ISOException.throwIt(ISO7816.SW_WRONG_DATA);
		}

		if (acces_areas[area] == 0x00 || current_area != 0x08) {
			ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
		}
		current_area = (byte) area;
	}// end of entry_in_area method

	public void exit_of_area(APDU apdu) {
		// access authentication
		if (!pin.isValidated()) {
			ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		}
		// if he is not in a room or in company
		if (current_area == 0x08 || in_company == 0x00) {
			ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);

		}
		current_area = 0x08;

	}// end of exit_of_area method

	public void exit_from_company(APDU apdu) {
		// access authentication
		if (!pin.isValidated()) {
			ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		}
		byte[] buffer = apdu.getBuffer();
		// inform system that the applet has finished
		// processing the command and the system should
		// now prepare to construct a response APDU
		// which contains data field
		short le = apdu.setOutgoing();

		// return 8 bytes
		// 2 for the hours worked in the current day,
		// 2 for the minutes worked in the current day
		// 2 for the hours worked in the current month,
		// 2 more for the minutes worked in the current month
		if (le < 4) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		} else {
			// informs the CAD the actual number of bytes
			// returned
			apdu.setOutgoingLength((byte) 4);

			// move the time worked data into the APDU buffer
			// starting at the offset 0
			end_time = TimeDuration.getInstance(JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT);

			SysTime.uptime(end_time);

			update_time();
			buffer[0] = (byte) (hours_worked_today);

			buffer[1] = (byte) (minutes_worked_today);

			buffer[2] = (byte) (hours_worked_current_month);

			buffer[3] = (byte) (minutes_worked_current_month);

			apdu.sendBytes((short) 0, (short) 4);

		}

	}// end of exit_from_company method

	public void update_time() {
		// calculate difference
		TimeDuration diff = end_time.minus(start_time);
		byte[] buffer = new byte[4];
		short offset = 0;
		short length = 4;
		diff.toBytes(buffer, offset, length, TimeDuration.MILLIS);
		int value = ((buffer[0] & 0xFF) << 24) | ((buffer[1] & 0xFF) << 16) | ((buffer[2] & 0xFF) << 8)
				| (buffer[3] & 0xFF);
		short minutes = (short) (value / (60 * 1000));
		short hours = (short) (minutes / 60);
		minutes = (short) (minutes % 60);
		hours_worked_today = (short) (hours_worked_today + hours);
		minutes_worked_today = (short) (minutes_worked_today + minutes);
		if (minutes_worked_today > 0x3b) {
			hours_worked_today = (short) ((minutes_worked_today / 60) + hours_worked_today);
			minutes_worked_today = (short) (minutes_worked_today % 60);
		}

	}// end of update_time method

	public void check_complete_work(APDU apdu) {
		// access authentication
		if (!pin.isValidated()) {
			ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		}
		byte[] buffer = apdu.getBuffer();
		// inform system that the applet has finished
		// processing the command and the system should
		// now prepare to construct a response APDU
		// which contains data field
		short le = apdu.setOutgoing();

		// return 8 bytes
		// 2 for the hours worked in the current day,
		// 2 for the minutes worked in the current day
		// 2 for the hours worked in the current month,
		// 2 more for the minutes worked in the current month
		if (le < 1) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		} else {
			// informs the CAD the actual number of bytes
			// returned
			apdu.setOutgoingLength((byte) 1);

			short flag;
			if ((short) 160 <= (short) (hours_worked_current_month & 0xFF)) {
				flag = (short) 0x01;
			} else {
				flag = (short) 0x00;
			}

			buffer[0] = (byte) (flag);
			apdu.sendBytes((short) 0, (short) 1);

		}
	}// end of check_complete_work method

	public void check_end_of_the_month(APDU apdu) {
		// access authentication
		if (!pin.isValidated()) {
			ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		}
		byte[] buffer = apdu.getBuffer();
		// inform system that the applet has finished
		// processing the command and the system should
		// now prepare to construct a response APDU
		// which contains data field

		// get lc
		byte lc = buffer[ISO7816.OFFSET_LC];

		byte byteRead = (byte) (apdu.setIncomingAndReceive());

		if (lc < 1 || byteRead < 1 || lc > 3 || byteRead > 3) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		}

		byte[] current_date = new byte[3];

		for (short i = 0; i < lc - 1; i++) {
			current_date[i] = buffer[(byte) (ISO7816.OFFSET_CDATA + i)];
		}

		short le = apdu.setOutgoing();
		if (le < 1) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		} else {
			// informs the CAD the actual number of bytes
			// returned
			apdu.setOutgoingLength((byte) 1);

			byte flag = (byte) 0x00;
			if (is_the_last_day(current_date)) {

				if ((short) 160 <= (short) (hours_worked_current_month & 0xFF))
					flag = (byte) 0x0a;
				else if ((short) (hours_worked_current_month & 0xFF) >= (short) 152) {
					flag = (byte) (PERFECTLY_WORKED - hours_worked_current_month);
				} else {
					flag = 0x09;
				}

				buffer[0] = (byte) (flag);
				apdu.sendBytes((short) 0, (short) 1);

			}
		}

	}// end of check_end_of_the_month method

	public boolean is_the_last_day(byte[] date) {
		byte day = date[0];
		byte month = date[1];
		byte year = date[2];
		if (month == (byte) 0x02 && (short) (year % 4) == 0 && day == (byte) 0x1c) {
			return true;
		} else if (day == (byte) 0x1d && month == (byte) 0x02 && (short) (year % 4) != 0) {
			return true;
		} else if (day == (byte) 0x1e
				&& (month == (byte) 0x04 || month == (byte) 0x06 || month == (byte) 0x09 || month == (byte) 0x0b)) {
			return true;
		} else if (day == (byte) 0x1f) {
			return true;
		}

		return false;
	}

} // end of class BadgeEmployee
