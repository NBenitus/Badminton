package javafx_gui;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import standings.StandingsCreationHelper;

class ChooseStandingsExcelFile extends WizardPage
{
	private Stage stage;

	public ChooseStandingsExcelFile(Stage stageOwner)
	{
		super("");
		stage = stageOwner;
	}

	Parent getContent()
	{
		VBox vBox = new VBox(10);
		vBox.setPadding(new Insets(30, 0, 30, 0));
		vBox.setSpacing(35);

		finishButton.setDisable(true);
		nextButton.setDisable(true);

		Label labelHeader = new Label("2. Sauvegarder fichier de classement");
		labelHeader.setFont(Font.font ("Segoe UI", FontWeight.BOLD, 16));
		Label standingsFileLabel = new Label("Aucun fichier choisi.");

		final Button openButton = new Button("Sauvegarder");

		openButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(final ActionEvent e)
			{
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls");
				fileChooser.getExtensionFilters().add(extFilter);
				File file = fileChooser.showSaveDialog(stage);

				if (file != null)
				{
					Alert alertIsOpenExcelFile = Dialog.verifyStandingsExcelFile(file);

					if (alertIsOpenExcelFile == null)
					{
						standingsFileLabel.setText("Fichier s�lectionn�: " + file);
						finishButton.setDisable(false);

						StandingsCreationHelper.setStandingsFile(file);
					}
					else
					{
						standingsFileLabel.setText("Aucun fichier s�lectionn�!");
						finishButton.setDisable(true);
					}
				}
			}
		});

		vBox.getChildren().add(labelHeader);
		vBox.getChildren().add(new Label("Sauvegarder le fichier Excel qui contiendra les classements des tournois"));
		vBox.getChildren().add(openButton);
		vBox.getChildren().add(standingsFileLabel);

		return vBox;
	}
}