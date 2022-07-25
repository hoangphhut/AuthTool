package AuthTool;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Studio extends Application {

	public static void main(String[] args) {
	    // Here you can work with args - command line parameters
	    Application.launch(args);
	}

	 @Override
	 public void start(Stage primaryStage) throws Exception {
	     primaryStage.setTitle("Trình biên soạn bài giảng");
	     BorderPane mainPane = new BorderPane();
	     Scene primaryScene = new Scene(mainPane, 1000,500);
	     primaryStage.setScene(primaryScene);
	     
	     //primaryStage.setWidth(300);
	     //primaryStage.setHeight(200);
	     
	     //Menu
	     MenuBar menubar = new MenuBar();
	     Menu FileMenu = new Menu("File");
	     MenuItem filemenu1=new MenuItem("new");
	     MenuItem filemenu2=new MenuItem("Save");
	     MenuItem filemenu3=new MenuItem("Exit");
	     Menu EditMenu=new Menu("Edit");
	     MenuItem EditMenu1=new MenuItem("Cut");
	     MenuItem EditMenu2=new MenuItem("Copy");
	     MenuItem EditMenu3=new MenuItem("Paste");
	     EditMenu.getItems().addAll(EditMenu1,EditMenu2,EditMenu3);	     
	     mainPane.setTop(menubar);  
	     FileMenu.getItems().addAll(filemenu1,filemenu2,filemenu3);  
	     menubar.getMenus().addAll(FileMenu,EditMenu); 
	     
	     // pane xử lý text to speech 
	     GridPane ttsPane = new GridPane();
	     
	     Scenario allText = new Scenario();
	     allText.initialize((int) primaryStage.getWidth());
	     mainPane.setCenter(allText.scrollPane);
	     
	     
	     primaryStage.show();
	     
	     
	     primaryStage.widthProperty().addListener((observable, oldValue, newValue) ->
         	allText.resize(newValue.intValue())
         	
	    		 );
	 }
	 
}
