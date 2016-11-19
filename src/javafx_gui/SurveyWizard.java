package javafx_gui;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import standings.StandingsCreationHelper;

/** This class shows a satisfaction survey */
class SurveyWizard extends Wizard
{
	Stage owner;

	public SurveyWizard(Stage owner)
	{
		super(new ChooseResultsExcelFile(owner), new ChooseStandingsExcelFile(owner));
		this.owner = owner;
	}

	public void finish()
	{
		Task task = new Task<Void>()
		{
			@Override
			public Void call() throws Exception
			{
				StandingsCreationHelper.createStandingsFile();
				return null;
			}
		};

		task.setOnFailed(e -> {
			Exception ex = (Exception) task.getException();
			Alert alertStandingsFileExecution = Dialog.getExceptionDialog(ex,
					"Une erreur est survenue lors de la création du ficher de résultats");
			alertStandingsFileExecution.showAndWait();
			owner.close();
		});

		task.setOnSucceeded(e -> owner.close());

		Scene scene = new Scene(new ProgressBarWindow(owner));
		owner.setScene(scene);
		owner.show();

		new Thread(task).start();
	}

	public void cancel()
	{
		System.out.println("Cancelled");
		owner.close();
	}
}