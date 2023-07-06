package cardObjects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadClientInterface;
import com.sun.javacard.apduio.CadDevice;
import com.sun.javacard.apduio.CadTransportException;

public class Card {
	static ArrayList<byte[]> byteArrayList;
	static CadClientInterface cad;
	static Socket sock;
	static Apdu apdu;
	static Process process;
	static ArrayList<String> log;

	public Card() throws IOException {
		apdu = new Apdu();
		log = new ArrayList<String>();
		byteArrayList = new ArrayList<byte[]>();
		open_cref_instance();
		try {
			open_cref_instance();

			make_connection();

			power_up();

			install_cap_files();

		} catch (Exception e) {
			close_cref_instance();
			System.out.println(e);
		}

		close_cref_instance();

	}

	private void get_commands() throws FileNotFoundException {
		File my_file = new File("C:\\Users\\Bordei Mihai Gabi\\eclipse-workspace\\Terminal\\resources\\cmds_apdu.txt");
		Scanner scanner = new Scanner(my_file);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String command = line;
			command = command.replace(";", "");
			String[] hexValues = command.split(" ");
			byte[] byteArray = new byte[hexValues.length];
			for (int i = 0; i < hexValues.length; i++) {
				byteArray[i] = (byte) Integer.parseInt(hexValues[i].substring(2), 16);
			}
			byteArrayList.add(byteArray);
		}
		scanner.close();

	}// end of get_commands method

	private void install_cap_files() throws IOException, CadTransportException {
		get_commands();
		for (byte[] command : byteArrayList) {
			apdu.command = command;
			apdu.setLc(command[4]);
			if (command[4] > (byte) 0x00) {
				byte[] data = new byte[(int) command[4]];
				System.arraycopy(command, 5, data, 0, (int) command[4]);
				apdu.setDataIn(data);
			}
			apdu.setLe((byte) 0x00);

			cad.exchangeApdu(apdu);

			log.add(apdu.toString());

		}
	}// end of install_cap_files method

	private void make_connection() throws UnknownHostException, IOException {
		sock = new Socket("localhost", 9025);
		InputStream is = sock.getInputStream();
		OutputStream os = sock.getOutputStream();
		cad = CadDevice.getCadClientInstance(CadDevice.PROTOCOL_T1, is, os);
	}// end of make_connection method

	private void open_cref_instance() throws IOException {
		String crefFilePath = "C:\\Program Files (x86)\\Oracle\\Java Card Development Kit Simulator 3.1.0\\bin\\cref.bat";
		process = Runtime.getRuntime().exec(crefFilePath);
	}// end of open_cref_instance method

	private void power_up() throws IOException, CadTransportException {
		byte[] ATR = cad.powerUp();
		log.add("powerUp");
		log.add(Arrays.toString(ATR));
	}// edn of power_up method

	public void power_down() throws IOException, CadTransportException {
		cad.powerDown();
		log.add("Power Down");
	}// end of power_down method

	public void close_connection() throws IOException {
		sock.close();

	}// end of close_connection method

	public void close_cref_instance() {
		process.destroy();
	}// end of close_cref_instance

	public void write_log() throws IOException {

		FileWriter fileWriter = new FileWriter(
				"C:\\Users\\Bordei Mihai Gabi\\eclipse-workspace\\Terminal\\resources\\log_apdu.txt");
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		PrintWriter printWriter = new PrintWriter(bufferedWriter);

		for (String element : log) {
			printWriter.println(element);
		}
		printWriter.close();

	}// end of write_log method

	private static void toAPDU(String command_String) {

		String command = command_String;
		command = command.replace(";", "");
		String[] hexValues = command.split(" ");
		byte[] byteArray = new byte[hexValues.length];
		for (int i = 0; i < hexValues.length; i++) {
			byteArray[i] = (byte) Integer.parseInt(hexValues[i].substring(2), 16);
		}
		apdu.command = byteArray;
		apdu.setLc(byteArray[4]);
		if (byteArray[4] > (byte) 0x00) {
			byte[] data = new byte[(int) byteArray[4]];
			System.arraycopy(byteArray, 5, data, 0, (int) byteArray[4]);
			apdu.setDataIn(data);
		}
		byte dim;
		if (byteArray[byteArray.length - 1] == (byte) 0x7F) {
			dim = (byte) 0x00;
		} else {
			dim = byteArray[byteArray.length - 1];
		}
		int i = dim & 0XFF;
		apdu.setLe((byte) dim);
	}// end of toAPDU method

	public Apdu executeCommand(String command) throws IOException, CadTransportException {
		toAPDU(command);
		cad.exchangeApdu(apdu);
		log.add(apdu.toString());
		return apdu;
	}// end of executeCommand method

}
