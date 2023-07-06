package graphicInterface;

import javafx.scene.control.ScrollPane;
import java.io.FileInputStream;
import java.io.IOException;
import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadTransportException;
import cardObjects.Card;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import utilObjects.DateProvider;
import utilObjects.Employee;
import utilObjects.Usecase;

public class CreateUsecasePage extends AnchorPane {
	public CreateUsecasePage(BorderPane root, Integer id, DateProvider date) throws IOException, CadTransportException {
		Employee employee = new Employee(id, date);
		Label titleLabel = new Label("Cardul de acces al lui " + employee.getName());
		titleLabel.getStyleClass().add("my-title");
		titleLabel.setAlignment(Pos.CENTER);
		root.setTop(titleLabel);

		Button button = new Button("    " + employee.getName());
		button.getStyleClass().add("my-card");
		ImageView imageView = new ImageView(new Image(
				new FileInputStream("C:\\Users\\Bordei Mihai Gabi\\eclipse-workspace\\Terminal\\resources\\user.png")));
		imageView.setFitWidth(100);
		imageView.setFitHeight(100);
		button.setGraphic(imageView);

		Label label1 = new Label(" Acesta este cardul tau    ");
		label1.getStyleClass().add("label1");

		Label label2 = new Label("Raspunsul la comanda de creare a cardului :  ");
		label2.getStyleClass().add("label2");

		Label label3 = new Label("Raspunsul la comanda de selectare a cardului :  ");
		label3.getStyleClass().add("label2");

		Card card = new Card();
		Usecase.employee = employee;
		Usecase.currentDate = date;
		Usecase.id = employee.getId();
		Apdu result;
		result = card.executeCommand(Usecase.createCommandForCreateApplet());
		Boolean flagIsCreated = Usecase.isExecuted(result);

		ScrollPane scrollLabel = new ScrollPane();

		Label longLabel = new Label(result.toString());
		longLabel.getStyleClass().add("long-label");
		scrollLabel.setContent(longLabel);
		scrollLabel.setFitToWidth(false);

		scrollLabel.setPrefViewportWidth(600);
		scrollLabel.setPrefViewportHeight(30);
		scrollLabel.setPrefHeight(10);

		ScrollPane scrollSelectLabel = new ScrollPane();
		result = card.executeCommand(
				"0x00 0xA4 0x04 0x00 0x0f 0x0A 0x00 0x00 0x00 0x00 0x0A 0x00 0x02 0x03 0x04 0x05 0x06 0x07 0x08 0x01 0x7F");
		Boolean flagIsSelected = Usecase.isExecuted(result);
		Label selectLabel = new Label(result.toString());
		selectLabel.getStyleClass().add("long-label");
		scrollSelectLabel.setContent(selectLabel);
		scrollSelectLabel.setFitToWidth(false);

		scrollSelectLabel.setPrefViewportWidth(600);
		scrollSelectLabel.setPrefViewportHeight(30);
		scrollSelectLabel.setPrefHeight(10);

		if (!flagIsCreated) {
			Label errorLabel = new Label("Cardul nu a fost creat  ");
			errorLabel.getStyleClass().add("error-label");

			setBottomAnchor(errorLabel, 200.0);
			setRightAnchor(errorLabel, 410.5);

			getChildren().add(errorLabel);
			card.power_down();
			card.close_cref_instance();
			card.close_connection();

		} else if (!flagIsSelected) {
			Label errorLabel = new Label("Cardul nu a fost selectat  ");
			errorLabel.getStyleClass().add("error-label");

			setBottomAnchor(errorLabel, 200.0);
			setRightAnchor(errorLabel, 410.5);

			getChildren().add(errorLabel);
			card.power_down();
			card.close_cref_instance();
			card.close_connection();
		} else {
			Button entryButton = new Button("întrare in companie");
			entryButton.getStyleClass().add("my-button");
			entryButton.setOnAction(event -> {

				try {
					GUIApplication.navigateToEntryInCompany(id, card);
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
			setBottomAnchor(entryButton, 70.0);
			setRightAnchor(entryButton, 10.0);
			getChildren().add(entryButton);
		}

		Button buttonBack = new Button("Inapoi");
		buttonBack.getStyleClass().add("my-button");
		BorderPane.setAlignment(buttonBack, Pos.BOTTOM_LEFT);
		buttonBack.setOnAction(event -> {
			try {
				card.power_down();
				card.close_connection();
			} catch (IOException | CadTransportException e) {
				e.printStackTrace();
			}
			card.close_cref_instance();
			try {
				GUIApplication.navigateToSelectDateMeniu();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		setBottomAnchor(label1, 600.0);
		setRightAnchor(label1, 720.5);

		setBottomAnchor(button, 450.0);
		setRightAnchor(button, 663.0);

		setBottomAnchor(label2, 400.0);
		setRightAnchor(label2, 530.0);

		setBottomAnchor(scrollLabel, 350.0);
		setRightAnchor(scrollLabel, 350.0);

		setBottomAnchor(label3, 300.0);
		setRightAnchor(label3, 503.0);

		setBottomAnchor(scrollSelectLabel, 250.0);
		setRightAnchor(scrollSelectLabel, 350.0);

		setBottomAnchor(buttonBack, 70.0);
		setRightAnchor(buttonBack, 850.0);

		getChildren().addAll(button, label1, label2, scrollLabel, label3, scrollSelectLabel, buttonBack);

	}

}
