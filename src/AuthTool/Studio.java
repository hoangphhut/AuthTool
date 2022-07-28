package AuthTool;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.robot.Robot;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Studio extends Application {
	public static String POWERPOINT = "C:/Program Files/Microsoft Office/root/Office16/POWERPNT.EXE";
	public static Stage pStage = null;
	public static Scenario sc = null;
	public static TextToSpeechController ttsDialog = null;
	public static Stage ttsDialogStage = null;
	public static Stage pActionDialogStage = null;
	public static MediaPlayer mediaPlayer = null;
	public static boolean PLAYING = false;

	public static void main(String[] args) {
	    // Here you can work with args - command line parameters
	    Application.launch(args);
	}

	 @Override
	 public void start(Stage primaryStage) throws Exception {
	     primaryStage.setTitle("Trình biên soạn bài giảng");
	     pStage = primaryStage;
	     
	     BorderPane mainPane = new BorderPane();
	     Scene primaryScene = new Scene(mainPane, 1000,500);
	     primaryStage.setScene(primaryScene);
	     	     
	     //Menu
	     MenuBar menubar = new MenuBar();
	     Menu FileMenu = new Menu("Kịch bản");
	     MenuItem filemenu1=new MenuItem("Tạo mới");
	     filemenu1.setOnAction(new EventHandler<ActionEvent>() {
	         public void handle(ActionEvent event) {
	        	 System.out.println("New file...");
        		 FileChooser fileChooser = new FileChooser();
        		 File file = fileChooser.showSaveDialog(Studio.pStage);
        		 if (file == null) return;
        		 
        		 sc = new Scenario(); // cần chuyển sang lúc Open hoăc New file
	             sc.FileName = file.getPath().replace("\\", "/");
	             sc.WorkingDir = sc.FileName.substring(0,sc.FileName.length() - file.getName().length() - 1); 
	             primaryStage.setTitle("Trình biên soạn bài giảng: " + sc.FileName);
	    	     
	             Paragraph p = new Paragraph();
	             p.initData("", null, null, null, null);
	             sc.initData(p);
	             sc.initUI(mainPane);	    	   
	             mainPane.setCenter(sc.scrollPane);	    	        
	             sc.gridPane.setVisible(true);
	             sc.resize((int)pStage.getWidth());
 	         }
	      });

	     MenuItem filemenu2=new MenuItem("Đọc từ file");
	     filemenu2.setOnAction(new EventHandler<ActionEvent>() {
	         public void handle(ActionEvent event) {
	        	 System.out.println("Open file...");
        		 FileChooser fileChooser = new FileChooser();
        		 File file = fileChooser.showOpenDialog(Studio.pStage);
        		 if (file == null) return;
        		 
        		 sc = new Scenario(); // cần chuyển sang lúc Open hoăc New file
	             sc.FileName = file.getPath().replace("\\", "/");
	             System.out.println("FileName: " + sc.FileName);
	             System.out.println("FileName: " + file.getName());
	             sc.WorkingDir = sc.FileName.substring(0,sc.FileName.length() - file.getName().length() - 1);
	             System.out.println("W.Dir: " + sc.WorkingDir);
	             primaryStage.setTitle("Trình biên soạn bài giảng: " + sc.FileName);
	             
	             
	    	     try {
	    	    	 String content = Files.readString(Path.of(sc.FileName));
	    	    	 JsonParser parser = new JsonParser();
	    	    	 JsonObject jObj = parser.parse(content).getAsJsonObject();
	    	    	 sc.fromJson(jObj);
	    	    	 sc.initUI(mainPane);
	    	    	 sc.debug();
	    	     } catch (Exception e) {
	    	    	 e.printStackTrace();
	    	     }
	    	     mainPane.setCenter(sc.scrollPane);	    	        
	             sc.gridPane.setVisible(true);
	             sc.resize((int)pStage.getWidth());
 	         }
	      });
	     MenuItem filemenu3=new MenuItem("Ghi ra file");
	     filemenu3.setOnAction(new EventHandler<ActionEvent>() {
	         public void handle(ActionEvent event) {
	        	 System.out.println("Save...");
	        	 if (sc.FileName == null) {
	        		 FileChooser fileChooser = new FileChooser();
	        		 File file = fileChooser.showSaveDialog(Studio.pStage);
	        		 if (file == null) return;
		             sc.FileName = file.getPath().replace("\\", "/");
		             primaryStage.setTitle("Trình biên soạn bài giảng: " + sc.FileName);
	        	 }
 
	        	 try {
	        		 Files.write(Paths.get(sc.FileName), sc.toJson().getBytes());	        	 
	        	 } catch (Exception e) {
	        		 e.printStackTrace();
	        	 }
	         }
	      });
	     MenuItem filemenu4=new MenuItem("Ghi ra file mới");
	     filemenu4.setOnAction(new EventHandler<ActionEvent>() {
	         public void handle(ActionEvent event) {
	        	 System.out.println("Save as...");
        		 FileChooser fileChooser = new FileChooser();
        		 File file = fileChooser.showSaveDialog(Studio.pStage);
        		 if (file == null) return;
	             sc.FileName = file.getPath().replace("\\", "/");
	             sc.WorkingDir = sc.FileName.substring(0,sc.FileName.length() - file.getName().length() - 1);
	             primaryStage.setTitle("Trình biên soạn bài giảng: " + sc.FileName);
 
	        	 try {
	        		 Files.write(Paths.get(sc.FileName), sc.toJson().getBytes());	        	 
	        	 } catch (Exception e) {
	        		 e.printStackTrace();
	        	 }
	         }
	      });

	     MenuItem filemenu5=new MenuItem("Kết thúc");
	     filemenu5.setOnAction(new EventHandler<ActionEvent>() {
	         public void handle(ActionEvent event) {
	    	     System.exit(0);
	         }
	     });
	     FileMenu.getItems().addAll(filemenu1,filemenu2,filemenu3, filemenu4, filemenu5);
	     FileMenu.getItems().add(2, new SeparatorMenuItem());
	     FileMenu.getItems().add(5, new SeparatorMenuItem());
	     
	     Menu EditMenu=new Menu("Trình diễn");
	     MenuItem EditMenu1=new MenuItem("Xem trước");
	     EditMenu1.setOnAction(new EventHandler<ActionEvent>() {
	         public void handle(ActionEvent event) {
	    	     Studio.present();
	         }
	     });
	     MenuItem EditMenu2=new MenuItem("Xem trước nhanh");
	     MenuItem EditMenu3=new MenuItem("Tạo Video");
	     EditMenu.getItems().addAll(EditMenu1,EditMenu2,EditMenu3);
	     
	     Menu OptionMenu=new Menu("Cấu hình");
	     MenuItem OptionMenu1=new MenuItem("Ứng dụng trình diễn");
	     MenuItem OptionMenu2=new MenuItem("Kết nối Vbee");
	     MenuItem OptionMenu3=new MenuItem("Kết nối Google");
	     OptionMenu.getItems().addAll(OptionMenu1,OptionMenu2,OptionMenu3);
	     mainPane.setTop(menubar);  	       
	     menubar.getMenus().addAll(FileMenu, EditMenu, OptionMenu); 
	     
	     // pane xử lý text to speech 
	     GridPane ttsPane = new GridPane();
	     
	     primaryStage.show();
	     	     
	     primaryStage.widthProperty().addListener((observable, oldValue, newValue) ->
         	sc.resize(newValue.intValue())
         	
	    		 );
	 }
	 
	 public static void present() {
		 if (Studio.sc == null || Studio.sc.all_paragraph == null || Studio.sc.all_paragraph.size() == 0) {
			 Alert alert = new Alert(AlertType.INFORMATION);
			 alert.setTitle("Trình diễn");
			 alert.setHeaderText("Không thể thực hiện trình diễn.");
			 String s ="Hãy tạo kịch bản trình diễn hoặc mở file trình diễn có sẵn.";
			 alert.setContentText(s);
			 alert.show();
			 return;
		 }
		 if (sc.all_paragraph.get(0).all_actions.size() == 0 || 
				 (sc.all_paragraph.get(0).all_actions.get(0).Action.compareTo(PreAction.PPT_OPEN) != 0)) {
			 Alert alert = new Alert(AlertType.INFORMATION);
			 alert.setTitle("Trình diễn");
			 alert.setHeaderText("Kịch bản trình diễn không hợp lý.");
			 String s ="Hoạt động đầu tiên trong kịch bản trình diễn cần được thiết lập là " + PreAction.PPT_OPEN 
					 + " cùng với file PPT.";
			 alert.setContentText(s);
			 alert.show();
			 return;
		 }

		 PreAction pA = sc.all_paragraph.get(0).all_actions.get(0);
		 if (pA.ActionPara == null || pA.ActionPara.length() == 0) {
			 Alert alert = new Alert(AlertType.INFORMATION);
			 alert.setTitle("Trình diễn");
			 alert.setHeaderText("Không có file trình diễn.");
			 String s ="Xem lại hoạt động đầu tiên của thuyết minh đầu tiên trong kịch bản, "
					 +"nhập đường dấn và tên file trình diễn vào mục 'Tham số'.";
			 alert.setContentText(s);
			 alert.show();
			 return;
		 }
		 
		 try {
			 System.out.println("Slide show action: " + pA.toString());
			 Process p = new ProcessBuilder(Studio.POWERPOINT, pA.ActionPara).start();	

			 //delay 4ms  
			 if (pA.Delay > 0) Thread.sleep(1000 * pA.Delay); 
			 
			 Robot robot = new Robot();
			 //reset các phím tổ hợp
			 robot.keyRelease(javafx.scene.input.KeyCode.SHIFT);
			 robot.keyRelease(javafx.scene.input.KeyCode.ALT);
			 robot.keyRelease(javafx.scene.input.KeyCode.CONTROL);
			 
			 for (int i=0; i<Studio.sc.all_paragraph.size(); i++) {
				 Paragraph par = Studio.sc.all_paragraph.get(i);
				 if (i == 0) {
					 par.present(robot, 1);
				 } else {
					 par.present(robot, 0);
				 }
				 Thread.sleep(2000);
				 //while (Studio.PLAYING) {
				//	 Thread.sleep(100);
				 //}
			 }
			 
		 } catch (Exception e) {     
			 e.printStackTrace();
			 return;
		 }   
	 }
}
