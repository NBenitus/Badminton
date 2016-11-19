package javafx_gui;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import excelHelper.ExcelFileReader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableWorkbook;
import utilities.Utilities;

public class Dialog
{

	public static Alert getExceptionDialog(Exception e, String message)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Erreur!");
		alert.setHeaderText(message);
		alert.setContentText("Veuillez contacter Benoît.");

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		return alert;
	}

	public static Alert verifyResultsExcelFile(File file)
	{
		Alert alert = null;

		if (Utilities.getFileExtension(file).equals("xlsx"))
		{
			alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Fichier invalide!");
			alert.setContentText("Les fichiers Excel de type \".xlsx\" ne peuvent être utilisés! \n"
					+ "Veuillez sauver le fichier de résultats en format \".xls\" !");
		}

		else if (!(Utilities.getFileExtension(file).equals("xls")))
		{
			alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Fichier invalide!");
			alert.setContentText("Le fichier sélectionné n'est pas de type Excel!");
		}
		else
		{
			try
			{
				ExcelFileReader.initialize(file);
			}
			catch (Exception e)
			{
				alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Fichier invalide!");
				alert.setContentText("Le fichier sélectionné est invalide! \n " + e.toString());
			}
		}

		return alert;
	}

	public static Alert verifyStandingsExcelFile(File file)
	{
		Alert alert = null;

		try
		{
			if(file.exists() && !(file.length() == 0) && !file.isDirectory())
			{
				Workbook workbook = Workbook.getWorkbook(file);;
				WritableWorkbook workbookCopy = Workbook.createWorkbook(file, workbook);
				workbook.close();
				workbookCopy.close();
			}
		}
		catch (BiffException | IOException ex)
		{
			alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Fichier ouvert!");
			alert.setContentText("Le fichier Excel sélectionné est présentment ouvert! Veuiller le fermer avant de procéder");

			alert.showAndWait();
		}
		catch (Exception e1)
		{
			alert = Dialog.getExceptionDialog(e1, "Une erreur est survenue lors de la sauvegarde du fichier de classements.");
			alert.showAndWait();
		}

		return alert;
	}
}
