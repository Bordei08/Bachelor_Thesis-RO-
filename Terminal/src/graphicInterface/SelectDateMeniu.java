package graphicInterface;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import utilObjects.DateProvider;

public class SelectDateMeniu extends FlowPane {
	public SelectDateMeniu(BorderPane root) {
		setAlignment(Pos.CENTER);
		setOrientation(Orientation.VERTICAL);
		setVgap(10);
		setHgap(10);

		Label titleLabel = new Label("Alege data");
		titleLabel.getStyleClass().add("page-title");
		BorderPane.setAlignment(titleLabel, Pos.CENTER);
		titleLabel.getStyleClass().add("my-title");
		root.setTop(titleLabel);

		Button currentDateButton = new Button("Data curentă");
		currentDateButton.getStyleClass().add("my-button");

		Button customDateButton = new Button("Data personalizată");
		customDateButton.getStyleClass().add("my-button");

		currentDateButton.setOnAction(event -> {
			DateProvider date = new DateProvider();
			GUIApplication.setDate(date);
			try {
				GUIApplication.navigateToEmployeesMeniu(date);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});

		FlowPane flowPane = new FlowPane();
		flowPane.getChildren().addAll(currentDateButton, customDateButton);
		flowPane.getStyleClass().add("my-flowpane");
		flowPane.setVgap(10);
		flowPane.setHgap(10);
		flowPane.setAlignment(Pos.CENTER);
		root.setCenter(flowPane);

		customDateButton.setOnAction(event -> {
			TextField yearField = new TextField("YYYY");
			yearField.getStyleClass().add("text-field");
			TextField monthField = new TextField("MM");
			monthField.getStyleClass().add("text-field");
			TextField dayField = new TextField("DD");
			dayField.getStyleClass().add("text-field");

			Button submitButton = new Button("Submit");
			submitButton.getStyleClass().add("my-button");

			submitButton.setOnAction(submitEvent -> {
				int year = Integer.parseInt(yearField.getText());
				int month = Integer.parseInt(monthField.getText());
				int day = Integer.parseInt(dayField.getText());

				if (isValidDate(year, month, day)) {
					DateProvider date = new DateProvider();
					date.setCustomDate(year, month, day);
					GUIApplication.setDate(date);
					try {
						GUIApplication.navigateToEmployeesMeniu(date);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					Label errorLabel = new Label();
					errorLabel.getStyleClass().add("error-label");
					errorLabel.setText("Data introdusă nu este corectă.");
					getChildren().add(errorLabel);
				}
			});

			getChildren().addAll(yearField, monthField, dayField, submitButton);

		});

		getChildren().addAll(titleLabel, currentDateButton, customDateButton);

	}

	private boolean isValidDate(int year, int month, int day) {
		try {
			LocalDate.of(year, month, day);
			return true;
		} catch (java.time.DateTimeException e) {
			return false;
		}
	}
}
