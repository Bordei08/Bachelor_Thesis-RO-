package graphicInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import com.sun.javacard.apduio.CadTransportException;
import cardObjects.Card;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import utilObjects.DateProvider;

public class GUIApplication extends Application {

	private static BorderPane root;
	private static Stage primaryStage;
	private static DateProvider date;

	@Override
	public void start(Stage primaryStage) throws IOException {
		GUIApplication.primaryStage = primaryStage;

		root = new BorderPane();
		navigateToSelectDateMeniu();

		Scene scene = new Scene(root, 1000, 700);
		scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		primaryStage.setTitle("Employee page");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void navigateToSelectDateMeniu() throws IOException {

		clearRoot();
		root.setCenter(new SelectDateMeniu(root));

	}

	public static void navigateToEmployeesMeniu(DateProvider date) throws FileNotFoundException {
		clearRoot();
		root.setCenter(new EmployeesMeniu(root, date));

	}

	public static void navigateToCreateUsecasePage(Integer id, DateProvider date)
			throws IOException, CadTransportException {
		clearRoot();
		root.setCenter(new CreateUsecasePage(root, id, date));
	}

	public static void navigateToEntryInCompany(Integer id, Card card) throws IOException {
		clearRoot();

		root.setCenter(new EntryInCompanyMeniu(root, id, card));

	}

	public static void navigateToCompany(Integer id, Card card) {
		clearRoot();
		root.setCenter(new InCompanyMeniu(root, id, card));
	}

	private static void clearRoot() {
		root.setTop(null);
		root.setBottom(null);
		root.setLeft(null);
		root.setRight(null);
		root.setCenter(null);
	}

	public static void setDate(DateProvider dateCustome) {
		date = dateCustome;
	}

	public static DateProvider getDate() {
		return date;
	}

}
