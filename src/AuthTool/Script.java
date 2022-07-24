package AuthTool;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/*
 * Class xử lý một đoạn mô tả slide sẽ được text-to-speech cùng với một số action Key hoặc Mouse 
 */
public class Script {
	public Scenario scenario; // link đến kịch bản (danh sách toàn bộ script)
	public List<SlideAction> slideAction; // các tương tác với slide trong script
	
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
	public TextArea text; // chứa text cần to speech 
	
	public void initialize(int textWidth) {
		mainPane = new BorderPane();
		
		topPane = new GridPane();
		topPane.setHgap(2);
		topPane.setVgap(2);
		addActionBtn = new Button("Tương tác");
	    topPane.addRow(0, addActionBtn);
	    toSpeechBtn = new Button("Thuyết minh");
	    topPane.addRow(0, toSpeechBtn);
	    previewBtn = new Button("Xem trước");
	    topPane.addRow(0, previewBtn);
	    topPane.addRow(1, new Button("..."));
	    topPane.addRow(1, new Label(new SlideAction(1).toString()));
	    topPane.addRow(2, new Button("..."));
	    topPane.addRow(2, new Label(new SlideAction(4).toString()));
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
		
		insertBtn = new Button("Chèn text");
		rightPane.addRow(1, insertBtn);
		insertBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	 
	    	 @Override
	    	 public void handle(ActionEvent arg0) {  
	    		 // TODO Auto-generated method stub  
	             System.out.println("Chèn text...");
	             scenario.insertText(insertBtn);
	         }  
	    });
		
		addBtn=new Button("Thêm text");  
		addBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	 
	    	 @Override
	    	 public void handle(ActionEvent arg0) {  
	    		 // TODO Auto-generated method stub  
	             System.out.println("Thêm mới text...");
	             scenario.addNewText();
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
	             scenario.deleteText(deleteBtn);
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
			    	    
	    text = new TextArea();
	    text.setPrefHeight(50);
	    text.setPrefWidth(textWidth);
	    mainPane.setCenter(text);    
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
}
