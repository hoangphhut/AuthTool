import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AuthTool extends Application {

	public static void main(String[] args) {
	    // Here you can work with args - command line parameters
	    Application.launch(args);
	}

	 @Override
	 public void start(Stage primaryStage) throws Exception {
	     primaryStage.setTitle("Hello world Application");
	     primaryStage.setWidth(300);
	     primaryStage.setHeight(200);

	     Label helloWorldLabel = new Label("Hello world!");
	     helloWorldLabel.setAlignment(Pos.CENTER);
	     Scene primaryScene = new Scene(helloWorldLabel);
	     primaryStage.setScene(primaryScene);

	     primaryStage.show();
	 }

}
