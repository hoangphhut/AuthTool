package AuthTool;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.robot.Robot;
import javafx.stage.Modality;
import javafx.stage.Stage;

/*
 * Class vừa đóng vai trò là Bean (chứa dữ liệu Paragraph), vừa là Controller (điểu khiển giao diện xử lý Paragraph)
 */
public class Paragraph {
	// các dữ liệu Bean
	public String text; // chứa text cần to speech
	public String audio_file;	
	public String audio_file_info;
	public String vbee_audio_url; // link file mp3 treen Vbee (giữ 1 tuần)
	public String vbee_request_id; 	
	
	
	public Vbee vbee; // object Vbee đang xử lý text to speech (nếu khác null)
	public Scenario scenario; // link đến kịch bản (danh sách toàn bộ script)
	public List<PreAction> all_actions; // các tương tác với slide trong paragraph
	
	// các dữ liệu Controller
	public GridPane topPane; // chứa các button xử lý nằm phía trên vùng text
	public Button addActionBtn;
	public Button toSpeechBtn;
	public Button previewBtn;
	
	public GridPane rightPane; // chứa các button xử lý nằm bên phải vùng text	
	public Button moveUpBtn; 
	public Button moveDownBtn;
	public Button addBtn; // button thêm text mới (vào cuối scenario)  
	public Button insertBtn; // button chèn text mới vào phía trước text hiện tại
	public Button deleteBtn;
	
	public BorderPane mainPane; // pane chứa các UI component để xử lý text
	public TextArea textArea;  // bị lẫn giữa Bean và Controller!!! nhưng sửa sau
	public List<Button> actionEditBtn; // các button để edit presentation action trong paragraph
	
	public void initData(String txt, String auFile, String vb_audio_url, String vb_request_id, List<PreAction> allA) {
		all_actions = allA;
		if (all_actions == null) all_actions = new ArrayList<PreAction>(); 
		System.out.println("1... " + all_actions.size());
		audio_file = auFile;
		vbee_audio_url = vb_audio_url;
		vbee_request_id = vb_request_id;	
		vbee = null;
		
	}
	/*
	 * Hàm khởi tạo giao diện xử lý Paragraph, đặt trong hàng thứ n của GridPane
	 */
	public void initUI(int n, GridPane parrent) {				
		mainPane = new BorderPane();
		parrent.add(mainPane, 0, n); // đưa paragraph vào vị trí cột 0, hàng n trong grid 
		
		topPane = new GridPane();
		topPane.setHgap(2);
		topPane.setVgap(2);
		
		addActionBtn = new Button("Thêm tương tác");
	    topPane.addRow(0, addActionBtn);
	    addActionBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	 
	    	 @Override
	    	 public void handle(ActionEvent arg0) {  
	    		 // TODO Auto-generated method stub  
	             System.out.println("Add action...");
	             scenario.addPresentationAction(addActionBtn);
	          }  
	    });
	    
	    toSpeechBtn = new Button("Thuyết minh");
	    topPane.addRow(0, toSpeechBtn);
	    toSpeechBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	 
	    	 @Override
	    	 public void handle(ActionEvent arg0) {  
	    		 // TODO Auto-generated method stub  
	             System.out.println("Text to speech...");
	             scenario.textToSpeechDialog(toSpeechBtn);
	          }  
	    });
	    
	    previewBtn = new Button("Xem trước");
	    topPane.addRow(0, previewBtn);
	    previewBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	 
	    	 @Override
	    	 public void handle(ActionEvent e) {  
	    		 // TODO Auto-generated method stub  
	             System.out.println("Preview paragraph...");
	             Paragraph p = Paragraph.search_paragraph_by_clicked_button((Button) e.getSource());
	             Robot r = new Robot();	             
	             p.present(r);
	          }  
	    });
	    
	    actionEditBtn = new ArrayList<Button>();
	    for (int i=0; i<all_actions.size(); i++) {
	    	PreAction pA = all_actions.get(i);
	    	Button b = new Button("(" + (i+1) + ")");
	    	b.setOnAction(new EventHandler<ActionEvent>() {
		    	 
		    	 @Override
		    	 public void handle(ActionEvent event) {
		    		 scenario.editPresentationAction((Button) event.getSource());
		          }  
		    });
	    	actionEditBtn.add(b);
	    	topPane.add(b, 0, i+1);
	    	topPane.add(new Label(pA.toString()),1, i+1, 2,1);
	    }
	    
	    mainPane.setTop(topPane);
		
		rightPane = new GridPane();
		rightPane.setVgap(2);
		rightPane.setHgap(2);
		moveUpBtn = new Button("Lên trên");
		rightPane.addRow(0, moveUpBtn);
		moveUpBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	 
	    	 @Override
	    	 public void handle(ActionEvent arg0) {  
	    		 // TODO Auto-generated method stub  
	             System.out.println("Move up...");
	             scenario.moveTextUp(moveUpBtn);
	         }  
	    });
		
		insertBtn = new Button("Chèn vào trước");
		rightPane.addRow(1, insertBtn);
		insertBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	 
	    	 @Override
	    	 public void handle(ActionEvent arg0) {  
	    		 // TODO Auto-generated method stub  
	             System.out.println("Chèn text...");
	             scenario.insertParagraph(insertBtn);
	         }  
	    });
		
		addBtn=new Button("Thêm vào sau");  
		addBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	 
	    	 @Override
	    	 public void handle(ActionEvent arg0) {  
	    		 // TODO Auto-generated method stub  
	             System.out.println("Thêm mới text...");
	             scenario.addNewParagraph();
	         }  
	    });
		rightPane.addRow(2, addBtn);
		
		deleteBtn=new Button("Xóa");
		rightPane.addRow(3, deleteBtn);
		deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	 
	    	 @Override
	    	 public void handle(ActionEvent arg0) {  
	    		 // TODO Auto-generated method stub  
	             System.out.println("Xóa text...");
	             scenario.deleteParagraph(deleteBtn);
	         }  
	    });
		
		moveDownBtn = new Button("Xuống dưới");
		rightPane.addRow(4, moveDownBtn);
		moveDownBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	 
	    	 @Override
	    	 public void handle(ActionEvent arg0) {  
	    		 // TODO Auto-generated method stub  
	             System.out.println("Move down...");
	             scenario.moveTextDown(moveDownBtn);
	         }  
	    });
		mainPane.setRight(rightPane);
			    	    
	    textArea = new TextArea();
	    textArea.setText(text);
	    textArea.setWrapText(true);
	    textArea.setPrefHeight(50);
	    textArea.setMinWidth(50);
	    mainPane.setCenter(textArea);    
	}
	
	/*
	 * Hàm hiển thị lại UI các Action (được gọi khi có thay đổi danh sách Action)
	 */
	public void validateActionUI() {
		ObservableList<Node> actionLine = topPane.getChildren();
		for (int i=actionLine.size()-1; i>=0; i--) {
			System.out.println("actionLine #" + i + ": " + actionLine.get(i).getId() + ", " + actionLine.get(i).toString());
			if (i>2) { //các action bắt đầu từ index #3
				actionLine.remove(i);
			}
		}
		
		actionEditBtn = new ArrayList<Button>();
	    for (int i=0; i<all_actions.size(); i++) {
	    	PreAction pA = all_actions.get(i);
	    	Button b = new Button("(" + (i+1) + ")");
	    	b.setOnAction(new EventHandler<ActionEvent>() {
		    	 
		    	 @Override
		    	 public void handle(ActionEvent event) {
		    		 scenario.editPresentationAction((Button) event.getSource());
		          }  
		    });
	    	actionEditBtn.add(b);
	    	topPane.add(b, 0, i+1);
	    	topPane.add(new Label(pA.toString()),1, i+1, 2,1);
	    }
	    
	}
	/*
	 * Hàm tắt/bật các button tùy theo vị trí của text trong scenatio có phải là đầu tiên
	 */
	public void showButtonFirst(boolean isFirstButton) {
		if (isFirstButton) {
			this.moveUpBtn.setDisable(true);
		} else {
			this.moveUpBtn.setDisable(false);
		}
	}
	public void showButtonLast(boolean isLastButton) {
		if (isLastButton) {
			this.moveDownBtn.setDisable(true);
			this.addBtn.setDisable(false);
		} else {
			this.moveDownBtn.setDisable(false);
			this.addBtn.setDisable(true);
		}
	}
	
	public String toJson() {
		String s = "";
		s = "{\"presentation_action\" : [";
		for (int i=0; i<all_actions.size(); i++) {
			PreAction pA = all_actions.get(i);
			s = s + pA.toJson();
			if (i < all_actions.size()-1) {
				s = s + ", ";
			} 
		}
		if (textArea != null) text = textArea.getText();
		if (text == null) text = "";
		s = s + "], ";
		s = s + "\"text\" : \"" + text + "\", "; 
		s = s + "\"audio_file\" : \"" + (this.audio_file != null ? this.audio_file : "") + "\",";
//		s = s + "\"audio_file_info\" : \"" + (this.audio_file_info != null ? this.audio_file_info : "") + "\",";
		s = s + "\"vbee_audio_url\" : \"" + (this.vbee_audio_url != null ? this.vbee_audio_url : "") + "\",";
		s = s + "\"vbee_request_id\" : \"" + (this.vbee_request_id != null ? this.vbee_request_id : "") + "\"";
		s = s + "}";
		return s;
	}
	public void fromJson(JsonObject jObj) {
		JsonArray jA = jObj.get("presentation_action").getAsJsonArray();
		all_actions = new ArrayList<PreAction>();
		for (int i=0; i<jA.size(); i++) {
			PreAction p = new PreAction();
			p.fromJson(jA.get(i).getAsJsonObject());
			all_actions.add(p);
			System.out.println("Paragraph.fromJson() got PAction #" + i + ": " + p.toString());
		}
		
		JsonElement jE = null;
		jE = jObj.get("text");
		if (jE != null)	this.text = jE.getAsString().trim();
		
		jE = jObj.get("audio_file");
		if (jE != null) {
			this.audio_file = jE.getAsString().trim();
			if (this.audio_file.length() == 0) this.audio_file = null;
		}
		
		/*
		jE = jObj.get("audio_file_info");
		if (jE != null)	{
			this.audio_file_info = jE.getAsString().trim();
			if (this.audio_file_info.length() == 0) this.audio_file_info = null;
		}		
		*/
		
		this.vbee_audio_url = jObj.get("vbee_audio_url").getAsString().trim();
		if (this.vbee_audio_url.length() == 0) this.vbee_audio_url = null;
		this.vbee_request_id = jObj.get("vbee_request_id").getAsString().trim();
		if (this.vbee_request_id.length() == 0) this.vbee_request_id = null;
		
		System.out.println("Paragraph.fromJson(): " + this.toJson());
	}
	
	/*
	 * Hàm tìm xem trong danh sách các paragraph (Studio.sc.all_paragraph), button được clicked thuộc paragraph nào.
	 * Nếu không thấy thì trả về null
	 */
	public static Paragraph search_paragraph_by_clicked_button(Button b) {
		for (int i=0; i<Studio.sc.all_paragraph.size(); i++) {
			Paragraph p = Studio.sc.all_paragraph.get(i);
			if (p.addActionBtn == b) return p;
			if (p.previewBtn == b) return p;
			if (p.toSpeechBtn == b) return p;
			
			if (p.addBtn == b) return p;
			if (p.deleteBtn == b) return p;
			if (p.insertBtn == b) return p;
			if (p.moveDownBtn == b) return p;
			if (p.moveUpBtn == b) return p;
			
			for (int j=0; j<p.actionEditBtn.size(); j++) // kiểm tra các button edit presentation action
				if (p.actionEditBtn.get(j) == b) return p; 
		}
		
		return null;
	}
	
	public static int search_presentation_action_by_clicked_button(Paragraph p, Button b) {
		for (int j=0; j<p.actionEditBtn.size(); j++) // kiểm tra các button edit presentation action
			if (p.actionEditBtn.get(j) == b) return j;
		
		return -1;
	}
	
	/*
	 * Hàm chỉ chạy các action, bắt đầu từ startAct, không phát audio.
	 * Nếu thông số delay = 0 thì delay theo từng Action
	 */
	public void quickShow(Robot robot, int startAct, int delay) {
		System.out.println(this.toString() + " -> quickShow() startAction: " + startAct);
		try {
			for (int j=startAct; j<all_actions.size(); j++) {
				PreAction pA = all_actions.get(j);
				System.out.println("-> action #" + j + ": " + pA.toString());
								
				if (pA.ActionGroup.compareTo(PreAction.KEY) == 0) {
					 if (pA.Action.compareTo(PreAction.F5) == 0) robot.keyPress(javafx.scene.input.KeyCode.F5);
					 if (pA.Action.compareTo(PreAction.ENTER) == 0) robot.keyPress(javafx.scene.input.KeyCode.ENTER);
					 if (pA.Action.compareTo(PreAction.LEFT) == 0) robot.keyPress(javafx.scene.input.KeyCode.LEFT);
					 if (pA.Action.compareTo(PreAction.RIGHT) == 0) robot.keyPress(javafx.scene.input.KeyCode.RIGHT);
					 if (pA.Action.compareTo(PreAction.PGUP) == 0) robot.keyPress(javafx.scene.input.KeyCode.PAGE_UP);
					 if (pA.Action.compareTo(PreAction.PGDN) == 0) robot.keyPress(javafx.scene.input.KeyCode.PAGE_DOWN);
					 
					 if (pA.Action.compareTo(PreAction.SHIFT_F5) == 0) {
						 robot.keyPress(javafx.scene.input.KeyCode.SHIFT);							 
						 robot.keyPress(javafx.scene.input.KeyCode.F5);
						 //Thread.sleep(100);
						 robot.keyRelease(javafx.scene.input.KeyCode.SHIFT);
						 robot.keyRelease(javafx.scene.input.KeyCode.F5);
					 }
					 
				 } else if (pA.ActionGroup.compareTo(PreAction.APP) == 0) {
					 if (pA.Action.compareTo(PreAction.PPT_CLOSE) == 0) {
						 Studio.stopPresentation(robot);
					 }
				 }
				if (delay > 0) {
					Thread.sleep(delay);
				} else if (pA.Delay > 0) {
					Thread.sleep(pA.Delay);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void present(Robot robot) {
		if (robot == null) robot = new Robot();
		
		System.out.println("Quick jump to Paragraph...");
		Studio.presentationProcess = Studio.sc.jumpToPreview(robot, this);
		if (Studio.presentationProcess == null) return;

		System.out.println("Bắt đầu play audio: " + audio_file);
		Studio.audioParagraph = new AudioParagraph();
		Studio.audioParagraph.initalize(this);
		Studio.audioParagraph.stopPresentationByComplete = true;
		Studio.audioParagraph.robot = robot;
		Studio.audioParagraph.continueNextParagraph = true;
		Studio.audioParagraph.start();

	}
}
