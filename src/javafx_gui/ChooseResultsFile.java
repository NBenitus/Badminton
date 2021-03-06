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
import standings.ResultFile;
import standings.StandingsCreationHelper;

class ChooseResultsFile extends WizardPage
{
	private Stage stage;

	/**
	  * Constructor
	  *
	  * @param stageOwner
	  *            stage object used to display the page
	  */
	public ChooseResultsFile(Stage stageOwner)
	{
		super("");
		stage = stageOwner;
	}

	/**
	 * Method to add GUI objects to be displayed upon viewing
	 */
	Parent getContent()
	{
		VBox vBox = new VBox(10);
		vBox.setPadding(new Insets(30, 0, 30, 0));
		vBox.setSpacing(25);

		// Disable the Next and Finish buttons
		nextButton.setDisable(true);
		finishButton.setDisable(true);

		FileChooser fileChooser = new FileChooser();
		final Button openButton = new Button("Sélectionner le fichier de résultats");
		Label resultsFileLabel = new Label("Aucun fichier choisi.");

		// Add a File chooser object upon click of the Open Button
		openButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(final ActionEvent e)
			{
				configureFileChooser(fileChooser);
				File file = fileChooser.showOpenDialog(stage);

				// Check if file provided is not null (should not happen)
				if (file != null)
				{
					// Check if the file is a valid XLS file
					Alert alert = Dialog.verifyResultsExcelFile(file);

					// Set results file in the StandingsCreationHelper class
					if (alert == null)
					{
						nextButton.setDisable(false);
						resultsFileLabel.setText("Fichier sélectionné: " + file);

						StandingsCreationHelper.setResultFile(new ResultFile(file));
					}

					// The file provided is invalid, show error message
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

		// Add all other objects to the VBox object
		vBox.getChildren().add(labelHeader);
		vBox.getChildren().add(labelDescription);
		vBox.getChildren().add(openButton);
		vBox.getChildren().add(resultsFileLabel);

		return vBox;
	}

	/**
	  * Configures different parameters (like the title or the extension filters associated to it) of a file chooser
	  *
	  * @param fileChooser
	  *            fileChooser object to be configured
	  */
	private static void configureFileChooser(final FileChooser fileChooser)
	{
		// Set title
		fileChooser.setTitle("Excel files");

		//Set initial directory
		fileChooser.setInitialDirectory(
				new File(System.getProperty("C:\\Users\\admin\\Desktop\\RSEQ\\2016-2017\\Sports\\Badminton\\Résultats\\")));

		//Add the xls extension filter if it does not already exist
		if (fileChooser.getExtensionFilters().size() == 0)
		{
			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XLS", "*.xls"),
					new FileChooser.ExtensionFilter("XLSX", "*.xlsx"));
		}
	}
}