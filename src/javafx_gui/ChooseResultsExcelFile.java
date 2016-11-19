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

class ChooseResultsExcelFile extends WizardPage
{
	private Stage stage;

	public ChooseResultsExcelFile(Stage stageOwner)
	{
//		super("1. Sélectionner fichier de résultats");
		super("");
		stage = stageOwner;
	}

	Parent getContent()
	{
		VBox vBox = new VBox(10);
		vBox.setPadding(new Insets(30, 0, 30, 0));
		vBox.setSpacing(25);

		nextButton.setDisable(true);
		finishButton.setDisable(true);

		FileChooser fileChooser = new FileChooser();
		final Button openButton = new Button("Sélectionner le fichier de résultats");
		Label resultsFileLabel = new Label("Aucun fichier choisi.");

		openButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(final ActionEvent e)
			{
				configureFileChooser(fileChooser);
				File file = fileChooser.showOpenDialog(stage);

				if (file != null)
				{
					Alert alert = Dialog.verifyResultsExcelFile(file);

					if (alert == null)
					{
						nextButton.setDisable(false);
						resultsFileLabel.setText("Fichier sélectionné: " + file);

						StandingsCreationHelper.setResultsFile(file);
					}
					else
					{
						alert.showAndWait();
					}
				}
			}
		});


		Label labelHeader = new Label("1. Sélectionner fichier de résultats");
		labelHeader.setFont(Font.font ("Segoe UI", FontWeight.BOLD, 16));

		Label labelDescription = new Label(
				"Sélectionner le fichier Excel qui contient la liste des résultats des tournois. (fourni par Francis). "
				+ "Exemple: C:\\Badminton\\Résultats16-17Tournoi1.xls.");
		labelDescription.setWrapText(true);

		vBox.getChildren().add(labelHeader);
		vBox.getChildren().add(labelDescription);
		vBox.getChildren().add(openButton);
		vBox.getChildren().add(resultsFileLabel);

		return vBox;
	}

	private static void configureFileChooser(final FileChooser fileChooser)
	{
		fileChooser.setTitle("Excel files");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

		if (fileChooser.getExtensionFilters().size() == 0)
		{
			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XLS", "*.xls"),
					new FileChooser.ExtensionFilter("All files", "*.*"));
		}
	}
}