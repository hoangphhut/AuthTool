package AuthTool;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.robot.Robot;
import javafx.stage.Modality;
import javafx.stage.Stage;

/*
 * Class nối nhiều OneText để thành một kịch bản trình bày bài giảng 
 */
public class Scenario {
	public static int COL_WIDTH_1 = 130; // độ rộng cột chưa các button
	public static List<Paragraph>all_paragraph = null;
	public static String FileName = null;
	public static String WorkingDir = null;
	
	public BorderPane parrent; //pane chứa scrollPane (là root của app)
	public ScrollPane scrollPane; // để chứa gridPane, hiển thị scroll tự động
	public GridPane gridPane; // để chứa các OneText, mỗi cái 1 dòng
	
	public String vbee_Authorization = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE2NTgxMzMwMjl9.mXlTv4WfK9C1rUFcUUgxfx_1QYjZg7UHrhM0FpOeVkc";
	public String vbee_app_id = "985eefdc-3a15-4949-a803-8979172fc6ca";
	public String vbee_callback_url = "http://daotao.ai:8080/vbee/api";
	public String vbee_voice_code = "hn_male_manhdung_news_48k-fhg";
	public String vbee_speed_rate = "0.8";
		
	/*
	 * Reset nội dung Scenario và hiển thị ban đâu (luôn có 1 paragraph)
	 */
	public void initUI(BorderPane p) {
		gridPane = new GridPane();
		gridPane.setVisible(false);
		scrollPane = new ScrollPane();
	    scrollPane.setContent(gridPane);
	    gridPane.setGridLinesVisible(true);
	    
	    this.parrent = p;
	    parrent.setCenter(scrollPane);
	    
	    for (int i=0; i<all_paragraph.size(); i++) {
	    	Paragraph par = all_paragraph.get(i);
			par.initUI(i, gridPane);
	    }
	    this.validateAllButtons();
	}
	/*
	 * Hàm reset dữ liệu Scenario. Luôn có paragraph đầu tiên
	 */
	public void initData(Paragraph firstP) {
		all_paragraph = new ArrayList<Paragraph>();
		
		if (firstP == null) {
			firstP = new Paragraph();
			firstP.initData("", null, null, null, null);
		}		
		firstP.scenario = this;	
		all_paragraph.add(firstP);
	}
	/*
	 * Hàm thiết lập lại các button của toàn bộ Text tùy theo vị trí (last/first)
	 */
	public void validateAllButtons() {
		for (int i=0; i<all_paragraph.size(); i++) {
			Paragraph oneT = all_paragraph.get(i);
			if (i == 0) {
				oneT.showButtonFirst(true);
			} else {
				oneT.showButtonFirst(false);
			}
			if (i == all_paragraph.size()-1) {
				oneT.showButtonLast(true);
			} else {
				oneT.showButtonLast(false);
			}
		}
	}
	public void addNewParagraph() {
		Paragraph oneP = new Paragraph();
		oneP.initData("", null, null, null, null);
		oneP.scenario = this;
		oneP.initUI(all_paragraph.size(), gridPane);
		all_paragraph.add(oneP);
		oneP.textArea.requestFocus();
		this.validateAllButtons();
		this.debug();
	}
	
	/*
	 * Hàm thêm thành phần Text mới vào phía trước thành phần chứa Button b (được click)
	 */
	public void insertParagraph(Button b) {
		Paragraph curP = Paragraph.search_paragraph_by_clicked_button(b);
		if (curP == null) return; // không tìm thấy thành phần text cần insert
		int index = Studio.sc.all_paragraph.indexOf(curP);
		
		for (int i=index; i<all_paragraph.size(); i++) {
			Paragraph p = all_paragraph.get(i);
			gridPane.setRowIndex(p.mainPane, gridPane.getRowIndex(p.mainPane) + 1); // dịch tất cả các hàng xuống dưới, kể từ Paragraph hiện tại
		}
		
		Paragraph oneP = new Paragraph(); // thêm paragraph mới để điền vào hàng uiIdx (đã trống)
		oneP.initData("", null, null, null, null);
		oneP.scenario = this;
		oneP.initUI(index, gridPane); //khi GUI hiển thị thì vị trí Paragraph trên list giống với data trong List

		all_paragraph.add(index, oneP); // thêm dữ liệu paragraph vào vị trí dataIdx
		oneP.textArea.requestFocus();
		this.validateAllButtons();
		this.debug();
	}

	/*
	 * Hàm xóa thành phần text trên scenbario tương ứng với button b được click
	 */
	public void deleteParagraph(Button b) {		
		if (Studio.sc.all_paragraph.size() == 1) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Xóa đoạn thuyết minh");
			alert.setHeaderText("Không thể xóa đoạn thuyết minh.");
			String s ="Kịch bản hiện tại có duy nhất một đoạn thuyết minh. Không được xóa đoạn thuyết minh này.";
			alert.setContentText(s);
			alert.show();
			return;
			 
		}
		
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Xóa đoạn thuyết minh");
		alert.setHeaderText("Bạn có chắc chắn muốn xóa đoạn thuyết minh này?");
		alert.setContentText("Lưu ý rằng file kịch bản vẫn lưu trữ đoạn thuyết minh dù đã bị xóa.\n"
				+"Đoạn thuyết minh sẽ bị xóa vĩnh viễn khỏi file trình chiếu khi bạn ghi kịch bản trình chiếu xuống file.");
		ButtonType yesButton = new ButtonType("Chắc chắn xóa", ButtonData.YES);
		ButtonType noButton = new ButtonType("Không xóa nữa", ButtonData.NO);
		alert.getButtonTypes().setAll(yesButton, noButton);
		ButtonType result = alert.showAndWait().orElse(noButton);
		if (result == noButton)return;

		
		Paragraph curP = Paragraph.search_paragraph_by_clicked_button(b);
		if (curP == null) return; // không tìm thấy thành phần text cần insert
		int index = Studio.sc.all_paragraph.indexOf(curP);
		
		gridPane.getChildren().remove(curP.mainPane); // khi xóa một cell thì các cell được tự động đẩy lên theo cấu trúc của grid		
		all_paragraph.remove(curP);
		// đánh lại rowIndex không cần thiết. Sau một số thao tác add/insert/delete thì các rowIndex không liền nhau (có khoảng trông), nhưng cũng không sao 
		//for (int i=index; i<all_paragraph.size(); i++) {
		//	Paragraph p = all_paragraph.get(i);
		//	gridPane.setRowIndex(p.mainPane, gridPane.getRowIndex(p.mainPane) - 1);
		//}
		
		this.validateAllButtons();
	    this.debug();
	}
	
	/*
	 * Hàm di chuyển text chứa Button b lên trên một hàng
	 */
	public void moveTextUp(Button b) {
		Paragraph curP = Paragraph.search_paragraph_by_clicked_button(b);
		if (curP == null) return; // không tìm thấy thành phần text cần insert
		int index = Studio.sc.all_paragraph.indexOf(curP);
		
		System.out.println("moveTextUp() #" + index);
		Paragraph oneT = all_paragraph.get(index);
		all_paragraph.remove(index);
		all_paragraph.add(index-1, oneT);
		
		int i = gridPane.getRowIndex(oneT.mainPane);
		gridPane.setRowIndex(oneT.mainPane, i-1);
		Paragraph t = all_paragraph.get(index);
		i = gridPane.getRowIndex(t.mainPane);
		gridPane.setRowIndex(t.mainPane, i+1);
		
		this.validateAllButtons();
		oneT.textArea.requestFocus();
		this.debug();
	}
	
	/*
	 * Hàm di chuyển text chứa Button b xuống dưới một hàng
	 */
	public void moveTextDown(Button b) {
		Paragraph curP = Paragraph.search_paragraph_by_clicked_button(b);
		if (curP == null) return; // không tìm thấy thành phần text cần insert
		int index = Studio.sc.all_paragraph.indexOf(curP);
		
		System.out.println("moveTextDown() #" + index);
		Paragraph oneT = all_paragraph.get(index);
		all_paragraph.remove(index);
		all_paragraph.add(index+1, oneT);
		
		int i = gridPane.getRowIndex(oneT.mainPane);
		gridPane.setRowIndex(oneT.mainPane, i+1);
		Paragraph t = all_paragraph.get(index);
		i = gridPane.getRowIndex(t.mainPane);
		gridPane.setRowIndex(t.mainPane, i-1);
		
		this.validateAllButtons();
		oneT.textArea.requestFocus();
		this.debug();
	}
	
	public void resize(int width) {
		for (int i=0; i<all_paragraph.size(); i++) {
		    Paragraph t = all_paragraph.get(i);
		    t.textArea.setPrefWidth(width - COL_WIDTH_1);
		}
	}
	
	public void debug() {
		/*
		System.out.print("Scenario:");
		for (int i = 0; i<all_paragraph.size(); i++) {
			Paragraph oneT = all_paragraph.get(i);
			System.out.print(" [#" + i + ": " + oneT.textArea.getText() + "]");
		}
		*/
		System.out.print("\nGridPane:");
		ObservableList<Node> paraLine = gridPane.getChildren();
		for (int i=0; i<paraLine.size(); i++) {
			Node nd = paraLine.get(i);
	        System.out.println(" #" + i + " " + "rowIndex: " + gridPane.getRowIndex(nd) + " " + nd.toString());
	    }
	    
	}
	
	public void addPresentationAction(Button b) {
		Paragraph par = Paragraph.search_paragraph_by_clicked_button(b);
		if (par == null) return;
		
		try {
			URL fxmlLocation = getClass().getResource("../ActionDialogBox.fxml");
			System.out.println("URL: " + fxmlLocation.toString());
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
			Parent parent = fxmlLoader.load();
			ActionDialogController controller = fxmlLoader.<ActionDialogController>getController();
			System.out.println("controller: " + controller);
			controller.WorkingMode = 0; //add new mode
			controller.initialize(par, null);
			Scene scene = new Scene(parent);
			Studio.pActionDialogStage = new Stage();
			Studio.pActionDialogStage.initModality(Modality.APPLICATION_MODAL);
			Studio.pActionDialogStage.setScene(scene);
			Studio.pActionDialogStage.setTitle("Thêm thao tác trình diễn");
			Studio.pActionDialogStage.showAndWait();
        } catch (Exception e) {
       	 	e.printStackTrace();
        }
	}
	
	public void editPresentationAction(Button b) {
		Paragraph par = Paragraph.search_paragraph_by_clicked_button(b);
		if (par == null) return;
		System.out.println("Found para: " + par.toJson());
   	 	int n = Paragraph.search_presentation_action_by_clicked_button(par, b);
   	 	if (n < 0) return;
   		System.out.println("Found edit button #" + n);
   	 
		try {
			URL fxmlLocation = getClass().getResource("../ActionDialogBox.fxml");
			System.out.println("URL: " + fxmlLocation.toString());
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
			Parent parent = fxmlLoader.load();
			ActionDialogController controller = fxmlLoader.<ActionDialogController>getController();
			System.out.println("controller: " + controller);
			controller.WorkingMode = 1; //edit mode
			controller.initialize(par, par.all_actions.get(n));
			Scene scene = new Scene(parent);
			Studio.pActionDialogStage = new Stage();
			Studio.pActionDialogStage.initModality(Modality.APPLICATION_MODAL);
			Studio.pActionDialogStage.setScene(scene);
			Studio.pActionDialogStage.setTitle("Sửa thao tác trình diễn");
			Studio.pActionDialogStage.showAndWait();
        } catch (Exception e) {
       	 	e.printStackTrace();
        }
	}

	public void textToSpeechDialog(Button b) {
		Paragraph p = Paragraph.search_paragraph_by_clicked_button(b);
		if (p == null) return;
		p.text = p.textArea.getText().trim();
		try {
			URL fxmlLocation = getClass().getResource("../TextToSpeech.fxml");
			System.out.println("URL: " + fxmlLocation.toString());
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
			Parent parent = fxmlLoader.load();
			Studio.ttsDialog = fxmlLoader.<TextToSpeechController>getController();
			System.out.println("controller: " + Studio.ttsDialog);       	 	
			Scene scene = new Scene(parent);
			Studio.ttsDialogStage = new Stage();
			Studio.ttsDialog.initToShow(p);
       	 	Studio.ttsDialogStage.initModality(Modality.APPLICATION_MODAL);
       	 	Studio.ttsDialogStage.setScene(scene);
       	 	Studio.ttsDialogStage.setTitle("Biên soạn dữ liệu âm thanh");
       	 	Studio.ttsDialogStage.showAndWait(); 
        } catch (Exception e) {
       	 	e.printStackTrace();
        }
	 }
	
	public String toJson() {
		String s = "";
		s = "{ \"vbee_Authorization\" : \"" + vbee_Authorization + "\", ";
		s = s + "\"vbee_app_id\" : \"" + vbee_app_id + "\", ";
		s = s + "\"vbee_callback_url\" : \"" + vbee_callback_url + "\", ";
		s = s + "\"vbee_voice_code\" : \"" + vbee_voice_code + "\", ";
		s = s + "\"vbee_speed_rate\" : " + vbee_speed_rate + ", ";
		s = s + "\"scenario\" : [";
		for (int i=0; i<all_paragraph.size()-1; i++) {
			Paragraph p = all_paragraph.get(i);
			s = s + p.toJson() + ",";
		}
		s = s + all_paragraph.get(all_paragraph.size()-1).toJson() + "] }";
		System.out.println("Scenario: " + s);
		return s;
	}

	/*
	 * Hàm này được gọi để load dữ liệu, không liên quan đến giao diện
	 */
	public void fromJson(JsonObject jObj) {
		try {
			vbee_Authorization = jObj.get("vbee_Authorization").getAsString();
			vbee_app_id = jObj.get("vbee_app_id").getAsString();
			vbee_callback_url = jObj.get("vbee_callback_url").getAsString();
			vbee_voice_code = jObj.get("vbee_voice_code").getAsString();
			vbee_speed_rate = jObj.get("vbee_speed_rate").getAsString();
			
			all_paragraph = new ArrayList<Paragraph>();
			JsonArray a = jObj.get("scenario").getAsJsonArray();
			for (int i=0; i<a.size(); i++) {
				Paragraph p = new Paragraph();
				p.fromJson(a.get(i).getAsJsonObject());
				p.scenario = this;
				all_paragraph.add(p);
			}
			
			System.out.println("Scenario: " + this.toJson());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	/*
	 * Hàm thực hiện trình diễn theo kịch bản, bắt đầu từ đoạn thuyết minh p.
	 * Chạy nhanh từ đầu đến paragraph p-1, sau đó show p.
	 * Trả về null nếu không thành công.
	 */
	public Process startPresentation(Robot robot, Paragraph p, int minDelay) {
		Process process = new Studio().startPresentation(minDelay);
		if (process == null) return null;
		
		//reset các phím có liên quan 
		robot.keyPress(javafx.scene.input.KeyCode.ESCAPE);
		robot.keyRelease(javafx.scene.input.KeyCode.SHIFT);
		robot.keyRelease(javafx.scene.input.KeyCode.ALT);
		robot.keyRelease(javafx.scene.input.KeyCode.CONTROL);
		
		
		for (int i=0; i<all_paragraph.size(); i++) {
			System.out.println("Quick jump to paragraph #" + i);
			Paragraph pp = all_paragraph.get(i);

			if (pp == p) minDelay = 0;
			if (i == 0)	{
				pp.quickShow(robot, 1, minDelay); // chạy từ action số 1 của pp vì pp là đầu (action đầu là mở file PPT)
			} else {
				pp.quickShow(robot, 0, minDelay); // chạy từ action số 0 
			}
			
			if (pp == p) return process;
		}
		return null;
	}
}
