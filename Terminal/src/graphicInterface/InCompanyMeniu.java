package graphicInterface;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadTransportException;
import cardObjects.Card;
import dataBase.DatabaseUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import utilObjects.Usecase;

public class InCompanyMeniu extends AnchorPane {
	public InCompanyMeniu(BorderPane root, Integer id, Card card) {

		DatabaseUtil db = new DatabaseUtil(GUIApplication.getDate());

		Label titleLabel = new Label("In companie");
		titleLabel.getStyleClass().add("page-title");
		BorderPane.setAlignment(titleLabel, Pos.CENTER);
		titleLabel.getStyleClass().add("my-title");
		root.setTop(titleLabel);

		FlowPane flowPane = new FlowPane();
		flowPane.getStyleClass().add("my-flowpane");
		TextField textField = new TextField();
		textField.getStyleClass().add("text-field");
		flowPane.getChildren().add(textField);

		Button getInfoButton = new Button("Informatiile tale");
		getInfoButton.getStyleClass().add("my-button");
		getInfoButton.setOnAction(event -> {
			getChildren().removeIf(node -> {
				Double x = AnchorPane.getRightAnchor(node);
				return x != null && x < 500;
			});
			// GUIApplication.navigateToCompany(id, card);
			Apdu result = new Apdu();
			try {
				result = card.executeCommand("0x80 0x70 0x01 0x01 0x00 0x6;");
			} catch (IOException | CadTransportException e) {
				e.printStackTrace();
			}

			Label commandLabel = new Label(result.toString());
			commandLabel.getStyleClass().add("long-label");
			ScrollPane scrollCommand = new ScrollPane();
			scrollCommand.setContent(commandLabel);
			scrollCommand.setFitToWidth(false);

			Label statusCmd = new Label();
			if (Usecase.isExecuted(result)) {
				statusCmd.getStyleClass().add("succes-label");
				statusCmd.setText("Comanda executata cu succes !");

				Label currentArea = new Label();
				currentArea.getStyleClass().add("label1");
				int aux = (int) result.getDataOut()[1];
				currentArea.setText(
						(aux == 8) ? "Momentan nu este in nicio zona" : "Momentan este in zona " + aux + "   ");

				Label numberOfHoursAndMinutesWorkedToday = new Label();
				numberOfHoursAndMinutesWorkedToday.getStyleClass().add("label1");
				int hours = (int) (result.getDataOut()[2] & 0xFF);
				int minutes = (int) (result.getDataOut()[3] & 0xFF);
				numberOfHoursAndMinutesWorkedToday.setText("Orele si minute lucrate astazi : " + hours + " : " + minutes
						+ " ( timp calculat de la ultima iesire )");

				Label numberOfHoursAndMinutesWorkedInCurrentMonth = new Label();
				numberOfHoursAndMinutesWorkedInCurrentMonth.getStyleClass().add("label1");
				hours = (int) (result.getDataOut()[4] & 0xFF);
				// minutes = (int)result.getDataOut()[5] & 0xFF;
				numberOfHoursAndMinutesWorkedInCurrentMonth.setText(
						"Numar de ore lucrate in luna curenta : " + hours + " ( timp calculat de la ultima iesire )");

				Label nameLabel = new Label("Informatiile lui " + db.getNameFromDB(id) + " : ");
				nameLabel.getStyleClass().add("label1");

				setBottomAnchor(nameLabel, 440.0);
				setRightAnchor(nameLabel, 450.5);

				setBottomAnchor(currentArea, 370.0);
				setRightAnchor(currentArea, 410.5);

				setBottomAnchor(numberOfHoursAndMinutesWorkedToday, 300.0);
				setRightAnchor(numberOfHoursAndMinutesWorkedToday, 53.5);

				setBottomAnchor(numberOfHoursAndMinutesWorkedInCurrentMonth, 230.0);
				setRightAnchor(numberOfHoursAndMinutesWorkedInCurrentMonth, 10.5);

				getChildren().addAll(nameLabel, currentArea, numberOfHoursAndMinutesWorkedToday,
						numberOfHoursAndMinutesWorkedInCurrentMonth);

			} else {
				statusCmd.getStyleClass().add("error-label");

				byte[] sw1sw2 = result.getSw1Sw2();

				if (sw1sw2[0] == (byte) 0x63 && sw1sw2[1] == (byte) 0x01) {
					statusCmd.setText("Cod PIN nevalidat");
					Label errorLabel = new Label("Trebuie sa verifici codul PIN");
					errorLabel.getStyleClass().add("label1");

					Button verfPin = new Button("Verifica PIN");
					verfPin.getStyleClass().add("my-button");
					verfPin.setOnAction(event4 -> {
						try {
							GUIApplication.navigateToEntryInCompany(id, card);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});

					setBottomAnchor(errorLabel, 440.0);
					setRightAnchor(errorLabel, 430.5);

					setBottomAnchor(verfPin, 370.0);
					setRightAnchor(verfPin, 430.5);

					getChildren().addAll(errorLabel, verfPin);

				} else {
					statusCmd.setText("Comanda invalida");
					Label errorLabel = new Label("  A aparut o eroare   ");
					errorLabel.getStyleClass().add("label1");
					setBottomAnchor(errorLabel, 440.0);
					setRightAnchor(errorLabel, 450.5);

					getChildren().add(errorLabel);

				}
			}

			setBottomAnchor(scrollCommand, 610.0);
			setRightAnchor(scrollCommand, 60.5);

			setBottomAnchor(statusCmd, 540.0);
			setRightAnchor(statusCmd, 280.5);

			getChildren().addAll(statusCmd, scrollCommand);

		});

		Button entryInAreaButton = new Button("Intra in zona");
		entryInAreaButton.getStyleClass().add("my-button");
		entryInAreaButton.setOnAction(event4 -> {
			getChildren().removeIf(node -> {
				Double x = AnchorPane.getRightAnchor(node);
				return x != null && x < 500;
			});
			Apdu result = new Apdu();
			int area = Integer.parseInt(textField.getText());
			try {
				result = card.executeCommand(Usecase.createCommandForEntryInArea(area));
			} catch (IOException | CadTransportException e) {
				e.printStackTrace();
			}
			Label commandLabel = new Label(result.toString());
			commandLabel.getStyleClass().add("long-label");
			ScrollPane scrollCommand = new ScrollPane();
			scrollCommand.setContent(commandLabel);
			scrollCommand.setFitToWidth(false);

			Label statusCmd = new Label();

			if (Usecase.isExecuted(result)) {
				statusCmd.getStyleClass().add("succes-label");
				statusCmd.setText("Comanda executata cu succes !");
				String pattern = "mm:HH:dd:MM:yyyy";
				Date currentDate = new Date();
				String pattern1 = "mm:HH:dd:MM:yyyy";
				SimpleDateFormat sdf = new SimpleDateFormat(pattern1);
				String time = sdf.format(currentDate);
				Label areaLabel = new Label(
						"Acces permis < " + (int) result.getDataIn()[0] + " > " + "< " + time + " >");
				areaLabel.getStyleClass().add("label1");

				setBottomAnchor(areaLabel, 440.0);
				setRightAnchor(areaLabel, 350.5);

				getChildren().add(areaLabel);
			} else {
				statusCmd.getStyleClass().add("error-label");

				byte[] sw1sw2 = result.getSw1Sw2();

				if (sw1sw2[0] == (byte) 0x63 && sw1sw2[1] == (byte) 0x01) {
					statusCmd.setText("Cod PIN nevalidat");
					Label errorLabel = new Label("Trebuie sa verifici codul PIN");
					errorLabel.getStyleClass().add("label1");

					Button verfPin = new Button("Verifica PIN");
					verfPin.getStyleClass().add("my-button");
					verfPin.setOnAction(event5 -> {
						try {
							GUIApplication.navigateToEntryInCompany(id, card);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});

					setBottomAnchor(errorLabel, 440.0);
					setRightAnchor(errorLabel, 430.5);

					setBottomAnchor(verfPin, 370.0);
					setRightAnchor(verfPin, 430.5);

					getChildren().addAll(errorLabel, verfPin);

				} else {
					statusCmd.setText("Comanda invalida");
					Label errorLabel = new Label("Acces interzis in zona <  " + (int) result.getDataIn()[0] + ">");
					errorLabel.getStyleClass().add("label1");
					setBottomAnchor(errorLabel, 440.0);
					setRightAnchor(errorLabel, 250.5);

					getChildren().add(errorLabel);

				}
			}

			setBottomAnchor(scrollCommand, 590.0);
			setRightAnchor(scrollCommand, 100.5);

			setBottomAnchor(statusCmd, 540.0);
			setRightAnchor(statusCmd, 280.5);

			getChildren().addAll(statusCmd, scrollCommand);

		});

		Button exitAreaButton = new Button("Iesi din zona");
		exitAreaButton.getStyleClass().add("my-button");
		exitAreaButton.setOnAction(event6 -> {
			getChildren().removeIf(node -> {
				Double x = AnchorPane.getRightAnchor(node);
				return x != null && x < 500;
			});
			Apdu result = new Apdu();

			try {
				result = card.executeCommand("0x80 0x70 0x01 0x01 0x00 0x6;");
			} catch (IOException | CadTransportException e) {
				e.printStackTrace();
			}

			int currentArea = (int) result.getDataOut()[1];

			try {
				result = card.executeCommand("0x80 0x40 0x00 0x00 0x00 0x7F");
			} catch (IOException | CadTransportException e) {
				e.printStackTrace();
			}
			Label commandLabel = new Label(result.toString());
			commandLabel.getStyleClass().add("long-label");
			ScrollPane scrollCommand = new ScrollPane();
			scrollCommand.setContent(commandLabel);
			scrollCommand.setFitToWidth(false);

			Label statusCmd = new Label();

			if (Usecase.isExecuted(result)) {
				statusCmd.getStyleClass().add("succes-label");
				statusCmd.setText("Comanda executata cu succes !");
				Date currentDate = new Date();

				String pattern = "mm:HH:dd:MM:yyyy";
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				String time = sdf.format(currentDate);

				Label areaLabel = new Label("Iesire din zona < " + currentArea + " > < " + time + " >");
				areaLabel.getStyleClass().add("label1");

				setBottomAnchor(areaLabel, 440.0);
				setRightAnchor(areaLabel, 340.5);

				getChildren().add(areaLabel);
			} else {
				statusCmd.getStyleClass().add("error-label");

				byte[] sw1sw2 = result.getSw1Sw2();

				if (sw1sw2[0] == (byte) 0x63 && sw1sw2[1] == (byte) 0x01) {
					statusCmd.setText("Cod PIN nevalidat");
					Label errorLabel = new Label("Trebuie sa verifici codul PIN");
					errorLabel.getStyleClass().add("label1");

					Button verfPin = new Button("Verifica PIN");
					verfPin.getStyleClass().add("my-button");
					verfPin.setOnAction(event5 -> {
						try {
							GUIApplication.navigateToEntryInCompany(id, card);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});

					setBottomAnchor(errorLabel, 440.0);
					setRightAnchor(errorLabel, 430.5);

					setBottomAnchor(verfPin, 370.0);
					setRightAnchor(verfPin, 430.5);

					getChildren().addAll(errorLabel, verfPin);

				} else {
					statusCmd.setText("Comanda invalida");
					Label errorLabel = new Label("Nu te afli in nicio zona");
					errorLabel.getStyleClass().add("label1");
					setBottomAnchor(errorLabel, 440.0);
					setRightAnchor(errorLabel, 250.5);

					getChildren().add(errorLabel);

				}
			}

			setBottomAnchor(scrollCommand, 590.0);
			setRightAnchor(scrollCommand, 100.5);

			setBottomAnchor(statusCmd, 540.0);
			setRightAnchor(statusCmd, 280.5);

			getChildren().addAll(statusCmd, scrollCommand);
		});

		Button changePinButton = new Button("Schimba pin ul");
		changePinButton.getStyleClass().add("my-button");
		changePinButton.setOnAction(event7 -> {
			getChildren().removeIf(node -> {
				Double x = AnchorPane.getRightAnchor(node);
				return x != null && x < 500;
			});
			Apdu result = new Apdu();
			String newPin = textField.getText();

			try {
				result = card.executeCommand(Usecase.createCommandForChangePin(newPin));
			} catch (IOException | CadTransportException e) {
				e.printStackTrace();
			}

			Label commandLabel = new Label(result.toString());
			commandLabel.getStyleClass().add("long-label");
			ScrollPane scrollCommand = new ScrollPane();
			scrollCommand.setContent(commandLabel);
			scrollCommand.setFitToWidth(false);

			Label statusCmd = new Label();
			if (Usecase.isExecuted(result)) {
				db.updatePIN(id, newPin);
				statusCmd.getStyleClass().add("succes-label");
				statusCmd.setText("Comanda executata cu succes !");
				Label changePinLabel = new Label("Cod pin schimbat cu succes, verifica noul PIN");
				changePinLabel.getStyleClass().add("label1");
				Button verfPin = new Button("Verifica PIN");
				verfPin.getStyleClass().add("my-button");
				verfPin.setOnAction(event5 -> {
					try {
						GUIApplication.navigateToEntryInCompany(id, card);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});

				setBottomAnchor(changePinLabel, 440.0);
				setRightAnchor(changePinLabel, 150.5);

				setBottomAnchor(verfPin, 370.0);
				setRightAnchor(verfPin, 430.5);

				getChildren().addAll(changePinLabel, verfPin);

			} else {
				statusCmd.getStyleClass().add("error-label");

				byte[] sw1sw2 = result.getSw1Sw2();

				if (sw1sw2[0] == (byte) 0x63 && sw1sw2[1] == (byte) 0x01) {
					statusCmd.setText("Cod PIN nevalidat");
					Label errorLabel = new Label("Trebuie sa verifici codul PIN");
					errorLabel.getStyleClass().add("label1");

					Button verfPin = new Button("Verifica PIN");
					verfPin.getStyleClass().add("my-button");
					verfPin.setOnAction(event5 -> {
						try {
							GUIApplication.navigateToEntryInCompany(id, card);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});

					setBottomAnchor(errorLabel, 440.0);
					setRightAnchor(errorLabel, 410.5);

					setBottomAnchor(verfPin, 370.0);
					setRightAnchor(verfPin, 430.5);

					getChildren().addAll(errorLabel, verfPin);

				} else {
					statusCmd.setText("Comanda invalida");
					Label errorLabel = new Label("Probabil nu ai dat un PIN valid");
					errorLabel.getStyleClass().add("label1");
					setBottomAnchor(errorLabel, 440.0);
					setRightAnchor(errorLabel, 250.5);

					getChildren().add(errorLabel);

				}
			}
			setBottomAnchor(scrollCommand, 590.0);
			setRightAnchor(scrollCommand, 100.5);

			setBottomAnchor(statusCmd, 540.0);
			setRightAnchor(statusCmd, 280.5);

			getChildren().addAll(statusCmd, scrollCommand);
		});

		Button exitFromCompanyButtom = new Button("Iesire");
		exitFromCompanyButtom.getStyleClass().add("my-button");
		exitFromCompanyButtom.setOnAction(event -> {
			getChildren().removeIf(node -> {
				Double x = AnchorPane.getRightAnchor(node);
				return x != null && x < 900;
			});

			root.setTop(null);
			titleLabel.setText("Iesire");
			BorderPane.setAlignment(titleLabel, Pos.CENTER);
			titleLabel.getStyleClass().add("my-title");
			root.setTop(titleLabel);

			Button buttonBack = new Button("Inapoi");
			buttonBack.getStyleClass().add("my-button");
			BorderPane.setAlignment(buttonBack, Pos.BOTTOM_LEFT);
			buttonBack.setOnAction(event8 -> {
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

			Apdu result = new Apdu();

			try {
				result = (Apdu) card.executeCommand("0x80 0x50 0x00 0x00 0x00 0x04;");
			} catch (IOException | CadTransportException e) {
				e.printStackTrace();
			}

			Label commandLabel = new Label(result.toString());
			commandLabel.getStyleClass().add("long-label");
			ScrollPane scrollCommand = new ScrollPane();
			scrollCommand.setContent(commandLabel);
			scrollCommand.setFitToWidth(false);

			Label statusCmd = new Label();
			if (Usecase.isExecuted(result)) {
				statusCmd.getStyleClass().add("succes-label");
				statusCmd.setText("Comanda executata cu succes !");

				String text = "La revedere < " + db.getNameFromDB(id) + " >! Ai lucrat < "
						+ (int) (result.getDataOut()[0] & 0xFF) + " : " + (int) (result.getDataOut()[1] & 0xFF) + " >";

				int hours = (int) result.getDataOut()[0];
				int minutes = (int) result.getDataOut()[1];

				db.updateNewDate(id, hours, minutes);

				Label finalLabel = new Label(text);
				finalLabel.getStyleClass().add("label1");

				try {
					result = (Apdu) card.executeCommand(Usecase.createCommandForCheckLastDay());
				} catch (IOException | CadTransportException e) {
					e.printStackTrace();
				}

				Label commandLabel1 = new Label(result.toString());
				commandLabel1.getStyleClass().add("long-label");
				ScrollPane scrollCommand1 = new ScrollPane();
				scrollCommand1.setContent(commandLabel1);
				scrollCommand1.setFitToWidth(false);

				Label statusCmd1 = new Label();

				if (Usecase.isExecuted(result)) {
					statusCmd1.getStyleClass().add("succes-label");
					statusCmd1.setText("Comanda executata cu succes!");
					Label finalMonth = new Label();
					finalMonth.getStyleClass().add("label1");
					if (result.getLe() != 0) {
						int flag = (int) result.getDataOut()[0];
						if (flag == 10) {
							finalMonth.setText("Ai acumulat cele 160 de ore!");
							setBottomAnchor(finalMonth, 500.0);
							setRightAnchor(finalMonth, 370.5);

							getChildren().addAll(finalMonth);
						} else if (flag < 9) {
							finalMonth.setText("Mai ai de recuperat " + flag + " ore");
							setBottomAnchor(finalMonth, 500.0);
							setRightAnchor(finalMonth, 380.5);

							getChildren().addAll(finalMonth);
						} else {
							finalMonth.setText("Pentru neefectuarea orelor, primiti o penalizare de 10%");
							setBottomAnchor(finalMonth, 500.0);
							setRightAnchor(finalMonth, 222.5);

							getChildren().addAll(finalMonth);
						}

					} else {
						finalMonth.setText(" Inca nu este final de luna  ");
						setBottomAnchor(finalMonth, 500.0);
						setRightAnchor(finalMonth, 372.5);

						getChildren().addAll(finalMonth);
					}

				} else {
					statusCmd.getStyleClass().add("error-label");

					byte[] sw1sw2 = result.getSw1Sw2();

					if (sw1sw2[0] == (byte) 0x63 && sw1sw2[1] == (byte) 0x01) {
						statusCmd.setText("Cod PIN nevalidat");
						Label errorLabel = new Label("Trebuie sa verifici codul PIN");
						errorLabel.getStyleClass().add("label1");

						Button verfPin = new Button("Verifica PIN");
						verfPin.getStyleClass().add("my-button");
						verfPin.setOnAction(event5 -> {
							try {
								GUIApplication.navigateToEntryInCompany(id, card);
							} catch (IOException e) {
								e.printStackTrace();
							}
						});

						setBottomAnchor(errorLabel, 500.0);
						setRightAnchor(errorLabel, 395.5);

						setBottomAnchor(verfPin, 450.0);
						setRightAnchor(verfPin, 400.5);

						getChildren().addAll(errorLabel, verfPin);

					} else {
						statusCmd.setText("Comanda invalida");
						Label errorLabel = new Label("Aceasta comanda a generat o eroare");
						errorLabel.getStyleClass().add("label1");
						setBottomAnchor(errorLabel, 500.0);
						setRightAnchor(errorLabel, 345.5);

						getChildren().add(errorLabel);

					}
				}

				setBottomAnchor(finalLabel, 690.0);
				setRightAnchor(finalLabel, 275.5);

				setBottomAnchor(scrollCommand1, 600.0);
				setRightAnchor(scrollCommand1, 220.5);

				setBottomAnchor(statusCmd1, 550.0);
				setRightAnchor(statusCmd1, 395.5);

				getChildren().addAll(finalLabel, scrollCommand1, statusCmd1);

			} else {
				statusCmd.getStyleClass().add("error-label");

				byte[] sw1sw2 = result.getSw1Sw2();

				if (sw1sw2[0] == (byte) 0x63 && sw1sw2[1] == (byte) 0x01) {
					statusCmd.setText("Cod PIN nevalidat");
					Label errorLabel = new Label("Trebuie sa verifici codul PIN");
					errorLabel.getStyleClass().add("label1");

					Button verfPin = new Button("Verifica PIN");
					verfPin.getStyleClass().add("my-button");
					verfPin.setOnAction(event5 -> {
						try {
							GUIApplication.navigateToEntryInCompany(id, card);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});

					setBottomAnchor(errorLabel, 690.0);
					setRightAnchor(errorLabel, 350.5);

					setBottomAnchor(verfPin, 580.0);
					setRightAnchor(verfPin, 370.5);

					getChildren().addAll(errorLabel, verfPin);

				} else {
					statusCmd.setText("Comanda invalida");
					Label errorLabel = new Label("Aceasta comanda a generat o eroare");
					errorLabel.getStyleClass().add("label1");
					setBottomAnchor(errorLabel, 690.0);
					setRightAnchor(errorLabel, 350.5);

					getChildren().add(errorLabel);

				}
			}

			setBottomAnchor(scrollCommand, 790.0);
			setRightAnchor(scrollCommand, 180.5);

			setBottomAnchor(statusCmd, 740.0);
			setRightAnchor(statusCmd, 395.5);

			setBottomAnchor(buttonBack, 270.0);
			setRightAnchor(buttonBack, 850.0);

			getChildren().addAll(statusCmd, scrollCommand, buttonBack);
		});

		setBottomAnchor(getInfoButton, 600.0);
		setRightAnchor(getInfoButton, 771.5);

		setBottomAnchor(entryInAreaButton, 500.0);
		setRightAnchor(entryInAreaButton, 800.5);

		setBottomAnchor(exitAreaButton, 400.0);
		setRightAnchor(exitAreaButton, 800.5);

		setBottomAnchor(changePinButton, 300.0);
		setRightAnchor(changePinButton, 778.5);

		setBottomAnchor(exitFromCompanyButtom, 200.0);
		setRightAnchor(exitFromCompanyButtom, 835.5);

		setBottomAnchor(flowPane, 100.0);
		setRightAnchor(flowPane, 531.5);

		getChildren().addAll(getInfoButton, entryInAreaButton, exitAreaButton, changePinButton, exitFromCompanyButtom,
				flowPane);

	}
}
