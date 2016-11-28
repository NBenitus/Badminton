package javafx_gui;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

// Deprecated
class ListPlayersInFolderPage extends WizardPage
{

	public ListPlayersInFolderPage()
	{
		super("Listes de joueurs");

//		nextButton.setDisable(true);
		finishButton.setDisable(true);
	}

	Parent getContent()
	{
		VBox vBox = new VBox(20);
		vBox.setPadding(new Insets(20,0,20,0));
		vBox.getChildren().add(new Label("Veuillez déplacer toutes les listes de joueurs dans le même dossier. Example: C:\\Badminton\\."));

		File file = new File("C:/Benoit/Work/Java/Badminton/Folder_List_Players.png");
	    Image image = new Image(file.toURI().toString());
	    ImageView iv = new ImageView(image);

		vBox.getChildren().add(iv);
		return vBox;
	}

	void nextPage()
	{
		super.nextPage();
	}
}

