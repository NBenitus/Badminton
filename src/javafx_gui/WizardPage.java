package javafx_gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.LabelBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** basic wizard page class */
@SuppressWarnings("deprecation")
abstract class WizardPage extends VBox
{
	Button priorButton = new Button("_Précédent");
	Button nextButton = new Button("S_uivant");
	Button cancelButton = new Button("Aborter");
	Button finishButton = new Button("_Compléter");

	WizardPage(String title)
	{
		getChildren()
				.add(LabelBuilder.create().text(title).style("-fx-font-weight: bold; -fx-padding: 0 0 5 0;").build());
		setId(title);
		setSpacing(5);
		setStyle(
				"-fx-padding:10; -fx-background-color: honeydew; -fx-border-color: derive(honeydew, -30%); -fx-border-width: 3;");

		Region spring = new Region();
		VBox.setVgrow(spring, Priority.ALWAYS);

//		Image image = new Image("file:files/RSEQ_Logo.png");

		Image image = new Image(WizardPage.class.getResourceAsStream("resources/RSEQ_Logo.png"));

        ImageView iv1 = new ImageView();
        iv1.setImage(image);

		VBox imageVBox = new VBox();
		imageVBox.getChildren().addAll(iv1);

        getChildren().addAll(imageVBox, getContent(), spring, getButtons());

		priorButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent actionEvent)
			{
				priorPage();
			}
		});
		nextButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent actionEvent)
			{
				nextPage();
			}
		});
		cancelButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent actionEvent)
			{
				getWizard().cancel();
			}
		});
		finishButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent actionEvent)
			{
				getWizard().finish();
			}
		});
	}

	HBox getButtons()
	{
		Region spring = new Region();
		HBox.setHgrow(spring, Priority.ALWAYS);
		HBox buttonBar = new HBox(5);
		cancelButton.setCancelButton(true);
		finishButton.setDefaultButton(true);
		buttonBar.getChildren().addAll(spring, priorButton, nextButton, cancelButton, finishButton);
		return buttonBar;
	}

	abstract Parent getContent();

	boolean hasNextPage()
	{
		return getWizard().hasNextPage();
	}

	boolean hasPriorPage()
	{
		return getWizard().hasPriorPage();
	}

	void nextPage()
	{
		getWizard().nextPage();
	}

	void priorPage()
	{
		getWizard().priorPage();
	}

	void navTo(String id)
	{
		getWizard().navTo(id);
	}

	Wizard getWizard()
	{
		return (Wizard) getParent();
	}

	public void manageButtons()
	{
		if (!hasPriorPage())
		{
			priorButton.setDisable(true);
		}

		if (!hasNextPage())
		{
			nextButton.setDisable(true);
		}
	}
}
