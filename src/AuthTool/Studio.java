package AuthTool;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.JsonElement;
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
	// Các thông tin được lưu trong Option:
	public static String POWERPOINT = "C:/Program Files/Microsoft Office/root/Office16/POWERPNT.EXE";
	//public static String POWERPOINT = "C:\\Program Files (x86)\\Microsoft Office\\root\\Office16\\POWERPNT.EXE";
	public static String LastOpenFile = null;
		
	public static Stage pStage = null;
	public static BorderPane mainPane = null;
	public static Scenario sc = null;
	public static TextToSpeechController ttsDialog = null;
	public static Stage ttsDialogStage = null;
	public static Stage pActionDialogStage = null;	
	public static Stage pOptionDialogStage = null;
	public static Process presentationProcess = null;
	public static AudioParagraph audioParagraph = null;

	public static void main(String[] args) {
		Studio.loadOption();		
	    Application.launch(args);
	}

	 @Override
	 public void start(Stage primaryStage) throws Exception {
	     primaryStage.setTitle("Trình biên soạn bài giảng");
	     pStage = primaryStage;	     
	     mainPane = new BorderPane();
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
	             Studio.LastOpenFile = sc.FileName;
	             Studio.saveOption();
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
        		  
        		 Studio.openFile(file.getPath());
        		 Studio.LastOpenFile = sc.FileName;
        		 Studio.saveOption();
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
	        	 Studio.LastOpenFile = sc.FileName;
	        	 Studio.saveOption();
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
	     
	     Menu PresentMenu=new Menu("Trình diễn");
	     MenuItem PresentMenu1=new MenuItem("Xem trước");
	     PresentMenu1.setOnAction(new EventHandler<ActionEvent>() {
	         public void handle(ActionEvent event) {
	    	     new Studio().startPresentation();
	         }
	     });
	     MenuItem EditMenu2=new MenuItem("Xem trước nhanh");
	     MenuItem EditMenu3=new MenuItem("Tạo Video");
	     MenuItem PresentMenu4=new MenuItem("Kết thúc trình diễn");
	     PresentMenu4.setOnAction(new EventHandler<ActionEvent>() {
	         public void handle(ActionEvent event) {
	        	 System.out.println("Kết thúc trình diễn...");
	    	     if (Studio.presentationProcess == null) return;
	    	     System.out.println("Destroy process...");
	    	     Studio.presentationProcess.destroy();

	    	     Studio.presentationProcess = null;
	    	     if (Studio.audioParagraph != null) {
	    	    	 Studio.audioParagraph.stop();
	    	    	 if (Studio.audioParagraph.mediaPlayer != null) {
	    	    		 System.out.println("Stop audio playing...");
	    	    		 Studio.audioParagraph.mediaPlayer.stop();
	    	    		 Studio.audioParagraph.mediaPlayer = null;
	    	    	 }			 
	    	     }
	         }
	     });
	     PresentMenu.getItems().addAll(PresentMenu1,EditMenu2,EditMenu3, PresentMenu4);
	     PresentMenu.getItems().add(3, new SeparatorMenuItem());
	     
	     Menu OptionMenu=new Menu("Cấu hình");
	     MenuItem OptionMenu1=new MenuItem("Cấu hình trình diễn");
	     OptionMenu1.setOnAction(new EventHandler<ActionEvent>() {
	         public void handle(ActionEvent event) {
	        	 System.out.println("Option setting...");
	    	     new Studio().optionDialog();
	         }
	     });
	     OptionMenu.getItems().addAll(OptionMenu1);
	     mainPane.setTop(menubar);	     
	     menubar.getMenus().addAll(FileMenu, PresentMenu, OptionMenu); 
	     
	     // pane xử lý text to speech 
	     GridPane ttsPane = new GridPane();
	     primaryStage.show();
	     if (Studio.LastOpenFile != null) Studio.openFile(Studio.LastOpenFile);
	     
	     	     
	     primaryStage.widthProperty().addListener((observable, oldValue, newValue) ->
         	sc.resize(newValue.intValue())
         	
	    		 );
	 }
	 
	 public void optionDialog() {
		 try {
			 //System.out.println(Studio.class.getClassLoader().getResource("/"));
			 URL fxmlLocation = getClass().getResource("../OptionView.fxml");
			 if (fxmlLocation == null) return;
			 System.out.println("URL: " + fxmlLocation.toString());
			 FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
			 Parent parent = fxmlLoader.load();
			 OptionDialogController controller = fxmlLoader.<OptionDialogController>getController();
			 System.out.println("controller: " + controller);
			 controller.initialize();
			 Scene scene = new Scene(parent);
			 Studio.pOptionDialogStage = new Stage();
			 Studio.pOptionDialogStage.initModality(Modality.APPLICATION_MODAL);
			 Studio.pOptionDialogStage.setScene(scene);
			 Studio.pOptionDialogStage.setTitle("Thiết lập các thông số cấu hình");
			 Studio.pOptionDialogStage.showAndWait();
		} catch (Exception e) {
	       	 	e.printStackTrace();
	    }
	 }
	 
	 /*
	  * Hàm kiểm tra kịch bản Scenario, chạy Powerpoint, mở file trình diễn.
	  * Trả về null nếu không thành công
	  */
	 public Process startPresentation() {
		 if (Studio.sc == null || Studio.sc.all_paragraph == null || Studio.sc.all_paragraph.size() == 0) {
			 Alert alert = new Alert(AlertType.INFORMATION);
			 alert.setTitle("Trình diễn");
			 alert.setHeaderText("Không thể thực hiện trình diễn.");
			 String s ="Hãy tạo kịch bản trình diễn hoặc mở file trình diễn có sẵn.";
			 alert.setContentText(s);
			 alert.show();
			 return null;
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
			 return null;
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
			 return null;
		 }
		 
		 try {
			 
			 System.out.println("Slide show action: " + pA.toString());
			 Process p = new ProcessBuilder(Studio.POWERPOINT, pA.ActionPara).start();	
 
			 if (pA.Delay > 0) Thread.sleep(pA.Delay); 
			 //Thread.sleep(3000);
			 return p;

		 } catch (Exception e) {     
			 e.printStackTrace();
			 return null;
		 }   
	 }
	 
	 public static void stopPresentation(Robot robot) {
		 try {
			 System.out.println(" -> Key Escape...");
			 robot.keyPress(javafx.scene.input.KeyCode.ESCAPE);
			 System.out.println(" -> delay 200");
			 Thread.sleep(200);
			 System.out.println(" -> Key Alt...");
			 robot.keyPress(javafx.scene.input.KeyCode.ALT);
			 //Thread.sleep(200);
			 System.out.println(" -> Key F4...");
			 robot.keyPress(javafx.scene.input.KeyCode.F4);
			 //Thread.sleep(200);
			 //System.out.println(" -> " + pA.toString() + " ReleaseKey F4...");
			 
			 //Thread.sleep(200);
			 System.out.println(" -> ReleaseKey Alt...");
			 robot.keyRelease(javafx.scene.input.KeyCode.ALT);
			 System.out.println(" -> ReleaseKey F4...");
			 robot.keyRelease(javafx.scene.input.KeyCode.F4);
			 System.out.println(" -> delay 200");
			 Thread.sleep(200);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 
	 }
	 
	 public static void openFile(String fN) {
		 System.out.println("openFile: " + fN);
		 
		 sc = new Scenario(); 
		 sc.FileName = fN.replace("\\", "/");
		 int n = sc.FileName.lastIndexOf("/");
         sc.WorkingDir = sc.FileName.substring(0,n);
         System.out.println("W.Dir: " + sc.WorkingDir);
         Studio.pStage.setTitle("Trình biên soạn bài giảng: " + sc.FileName);         
         
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
	 
	 public static void saveOption() {
		 Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
		 System.out.println("Working dir: " + path.toString());
		 String oF = path.toString() + "/" + "option.json";
		 try {
			 String s = "{"
					 + "\"POWERPOINT\" : \"" + Studio.POWERPOINT.replace("\\", "/") + "\"";
			 if (Studio.LastOpenFile != null) {
				 s = s + ", \"LastOpenFile\" : \"" + Studio.LastOpenFile.replace("\\", "/") + "\" }";
			 } else {
				 s = s + "}";
			 }
			 Files.write(Paths.get(oF), s.getBytes());
		  } catch (Exception e) {
		   	 e.printStackTrace();
		  }
	 }
	 
	 public static void loadOption() {
		 Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
		 System.out.println("Working dir: " + path.toString());
		 String oF = path.toString() + "/" + "option.json";
		 try {
			 Path p = Path.of(oF);
			 String oS = Files.readString(p);
			 
		     JsonParser parser = new JsonParser();
		     JsonObject jObj = parser.parse(oS).getAsJsonObject();
		     
		     JsonElement jE = jObj.get("POWERPOINT");
		     if (jE != null) {
		    	 Studio.POWERPOINT = jE.getAsString().trim();
		    	 System.out.println("POWERPOINT: " + Studio.POWERPOINT);
		     }
		    	 
		     jE = jObj.get("LastOpenFile");
		     if (jE != null ) {
		    	 Studio.LastOpenFile = jE.getAsString().trim();
		    	 System.out.println("LastOpenFile: " + Studio.LastOpenFile);
		     }
		  } catch (Exception e) {
		   	 System.out.println("Không đọc được file cấu hình: " + oF);
		   	System.out.println("Chương trình có thể không hoạt động tốt nếu thiếu một số thông số cấu hình!");
		  }
	 }
}
