package javafx_gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ProgressBarWindow extends VBox
{
	/**
	  * Constructor
	  *
	  * @param stageOwner
	  *            stage object used to display the page
	  */
	public ProgressBarWindow(Stage stage)
	{
        final ProgressBar pb = new ProgressBar();
        pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        Label label = new Label("Le fichier de classement est en cours de création.");
        label.setFont(Font.font ("Segoe UI", FontWeight.BOLD, 16));

        setStyle(
				"-fx-padding:10; -fx-background-color: honeydew; -fx-border-color: derive(honeydew, -30%); -fx-border-width: 3;");

        setPadding(new Insets(20, 20, 20, 20));
        setAlignment(Pos.BASELINE_CENTER);
        setSpacing(25);

        getChildren().addAll(label);
        getChildren().addAll(pb);
	}
}
