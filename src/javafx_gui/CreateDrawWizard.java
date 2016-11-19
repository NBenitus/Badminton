package javafx_gui;

import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.Stage;

/** This class displays a survey using a wizard */
public class CreateDrawWizard extends Application
{
	public static void main(String[] args) throws Exception
	{
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception
	{
		// configure and display the scene and stage.
		stage.setScene(new Scene(new SurveyWizard(stage), 500, 400));
		stage.show();
	}
}
