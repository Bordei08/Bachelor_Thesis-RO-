package graphicInterface;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.javacard.apduio.CadTransportException;

import dataBase.DatabaseUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import utilObjects.DateProvider;
import utilObjects.Employee;

public class EmployeesMeniu extends FlowPane {
	public EmployeesMeniu(BorderPane root, DateProvider date) throws FileNotFoundException {
		DatabaseUtil db = new DatabaseUtil(date);
		ArrayList<Employee> employees = new ArrayList<>();

		Label titleLabel = new Label("Angajatii");
		BorderPane.setAlignment(titleLabel, Pos.CENTER);
		titleLabel.getStyleClass().add("my-title");
		root.setTop(titleLabel);

		for (Integer id : db.getEmployeesId()) {
			employees.add(new Employee(id, date));
		}

		ArrayList<Button> buttons = new ArrayList<>();
		for (Employee employee : employees) {
			Button button = new Button("    " + employee.getName());
			button.getStyleClass().add("my-button");
			ImageView imageView = new ImageView(new Image(new FileInputStream(
					"C:\\Users\\Bordei Mihai Gabi\\eclipse-workspace\\Terminal\\resources\\user.png")));
			imageView.setFitWidth(100);
			imageView.setFitHeight(100);
			button.setGraphic(imageView);
			button.setOnAction(event -> {
				Integer id = employee.getId();
				try {
					GUIApplication.navigateToCreateUsecasePage(id,date);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CadTransportException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			});
			buttons.add(button);
		}

		Button buttonBack = new Button("Inapoi");
		buttonBack.getStyleClass().add("my-button");
		BorderPane.setAlignment(buttonBack, Pos.BOTTOM_LEFT);
		buttonBack.setOnAction(event -> {
			try {
				GUIApplication.navigateToSelectDateMeniu();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		 root.setBottom(buttonBack);
		//buttons.add(buttonBack);

		getChildren().addAll(buttons);
		getStyleClass().add("my-flowpane");
		setVgap(10);
		setHgap(10);
		setAlignment(Pos.CENTER);
	}
}
