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

	/**
	  * Constructor
	  *
	  * @param stageOwner
	  *            stage object used to display the page
	  */
	public SurveyWizard(Stage owner)
	{
		super(new ChooseResultsFile(owner), new ChooseStandingsFile(owner));
		this.owner = owner;
	}

	/**
	  * Method executed when the user completes the wizard
	  *
	  */
	public void finish()
	{
		Task<Void> task = new Task<Void>()
		{
			@Override
			public Void call() throws Exception
			{
				// Create the standings file
				StandingsCreationHelper.createStandingsFile();
				return null;
			}
		};

		// Display error message if creating the standing file fails
		task.setOnFailed(e -> {
			Exception ex = (Exception) task.getException();
			Alert alertStandingsFileExecution = Dialog.getExceptionDialog(ex,
					"Une erreur est survenue lors de la création du ficher de résultats");
			alertStandingsFileExecution.showAndWait();
			owner.close();
		});

		task.setOnSucceeded(e -> owner.close());

		// Display progress bar window while the standings file is created
		Scene scene = new Scene(new ProgressBarWindow(owner));
		owner.setScene(scene);
		owner.show();

		new Thread(task).start();
	}

	/**
	  * Method executed when the user cancels the wizard
	  *
	  */
	public void cancel()
	{
		System.out.println("Cancelled");
		owner.close();
	}
}