import java.io.IOException;
import com.sun.javacard.apduio.CadTransportException;
import graphicInterface.GUIApplication;
import javafx.application.Application;

public class Terminal {
	public static void main(String[] args) throws IOException, CadTransportException {
		Application.launch(GUIApplication.class, args);

	}
}