package graphicInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import utilObjects.Usecase;

import java.io.IOException;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadTransportException;

import cardObjects.Card;
import dataBase.DatabaseUtil;

public class EntryInCompanyMeniu extends AnchorPane {
	public EntryInCompanyMeniu(BorderPane root, Integer id, Card card) throws IOException {

		DatabaseUtil db = new DatabaseUtil(GUIApplication.getDate());

		Label titleLabel = new Label("Tasteaza codul PIN");
		titleLabel.getStyleClass().add("page-title");
		BorderPane.setAlignment(titleLabel, Pos.CENTER);
		titleLabel.getStyleClass().add("my-title");
		root.setTop(titleLabel);

		FlowPane flowPane = new FlowPane();
		flowPane.getStyleClass().add("my-flowpane");
		TextField textField = new TextField();
		textField.getStyleClass().add("text-field");
		flowPane.getChildren().add(textField);

		Button submitButton = new Button("Submit");
		submitButton.getStyleClass().add("my-button");
		submitButton.setOnAction(event2 -> {

			String pin = textField.getText();
			Apdu result = new Apdu();
			try {
				result = card.executeCommand(Usecase.createCommandForCheckPin(pin));
			} catch (IOException | CadTransportException e) {

				e.printStackTrace();
			}
			if (!Usecase.isExecuted(result)) {
				Label errorLabel = new Label("Cod PIN gresit, ai doar 3 incercari");
				errorLabel.getStyleClass().add("error-label");

				ScrollPane scrollLoginLabel = new ScrollPane();
				Label loginLabel = new Label(result.toString());
				loginLabel.getStyleClass().add("long-label");
				scrollLoginLabel.setContent(loginLabel);
				scrollLoginLabel.setFitToWidth(false);

				scrollLoginLabel.setPrefViewportWidth(520);
				scrollLoginLabel.setPrefViewportHeight(30);
				scrollLoginLabel.setPrefHeight(10);

				setBottomAnchor(errorLabel, 290.0);
				setRightAnchor(errorLabel, 381.5);

				setBottomAnchor(scrollLoginLabel, 340.0);
				setRightAnchor(scrollLoginLabel, 210.5);

				getChildren().addAll(errorLabel, scrollLoginLabel);

			} else {
				String name = db.getNameFromDB(id);
				Date currentDate = new Date();
				String pattern = "mm:HH:dd:MM:yyyy";
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				String time = sdf.format(currentDate);
				Label succesLabel = new Label("Bine ai venit ! < " + name + " " + time + " >");
				succesLabel.getStyleClass().add("succes-label");

				ScrollPane scrollLoginLabel = new ScrollPane();
				Label loginLabel = new Label(result.toString());
				loginLabel.getStyleClass().add("long-label");
				scrollLoginLabel.setContent(loginLabel);
				scrollLoginLabel.setFitToWidth(false);

				try {
					result = card.executeCommand("0x80 0x90 0x00 0x00 0x00 0x01;");
				} catch (IOException | CadTransportException e) {
					e.printStackTrace();
				}
				Label fullWorkLabel = new Label();
				Label resultCommandFullWork = new Label(result.toString());
				resultCommandFullWork.getStyleClass().add("label1");
				ScrollPane scrollWork = new ScrollPane();
				scrollWork.setContent(resultCommandFullWork);
				scrollWork.setFitToWidth(false);

				if (Usecase.isExecuted(result)) {
					int output = (int) result.getDataOut()[0];
					if (output == 1) {
						fullWorkLabel.getStyleClass().add("succes-label");
						fullWorkLabel.setText("Ai acumulat cele 160 de ore !");
						setBottomAnchor(fullWorkLabel, 180.0);
						setRightAnchor(fullWorkLabel, 390.5);
					} else {
						fullWorkLabel.getStyleClass().add("label1");
						fullWorkLabel.setText("Inca nu ai acumulat cele 160 de ore");
						setBottomAnchor(fullWorkLabel, 180.0);
						setRightAnchor(fullWorkLabel, 310.5);

					}

					Button entryButton = new Button("In companie");
					entryButton.getStyleClass().add("my-button");

					entryButton.setOnAction(event3 -> {
						GUIApplication.navigateToCompany(id, card);
					});

					setBottomAnchor(entryButton, 100.0);
					setRightAnchor(entryButton, 10.0);

					

					getChildren().addAll(fullWorkLabel, entryButton);
				} else {
					Label errorWork = new Label("Nu s a putut verifica numarul de ore !");
					errorWork.getStyleClass().add("error-label");

					setBottomAnchor(errorWork, 180.0);
					setRightAnchor(errorWork, 310.5);
					getChildren().add(errorWork);
				}

				scrollLoginLabel.setPrefViewportWidth(520);
				scrollLoginLabel.setPrefViewportHeight(30);
				scrollLoginLabel.setPrefHeight(10);

				setBottomAnchor(succesLabel, 290.0);
				setRightAnchor(succesLabel, 330.5);

				setBottomAnchor(scrollLoginLabel, 340.0);
				setRightAnchor(scrollLoginLabel, 210.5);

				setBottomAnchor(scrollWork, 240.0);
				setRightAnchor(scrollWork, 160.5);

				getChildren().addAll(succesLabel, scrollLoginLabel, scrollWork);
			}
		});
		Button buttonBack = new Button("Inapoi");
		buttonBack.getStyleClass().add("my-button");
		BorderPane.setAlignment(buttonBack, Pos.BOTTOM_LEFT);
		buttonBack.setOnAction(event1 -> {
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

		setBottomAnchor(flowPane, 490.0);
		setRightAnchor(flowPane, 124.5);

		setBottomAnchor(submitButton, 410.0);
		setRightAnchor(submitButton, 427.5);

		setBottomAnchor(buttonBack, 100.0);
		setRightAnchor(buttonBack, 850.0);

		getChildren().addAll(flowPane, submitButton, buttonBack);

	}
}
