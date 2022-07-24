package AuthTool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

/*
 * Class nối nhiều OneText để thành một kịch bản trình bày bài giảng 
 */
public class Scenario {
	public static int COL_WIDTH_1 = 130; // độ rộng cột chưa các button
	
	public GridPane gridPane; // để chứa các OneText, mỗi cái 1 dòng
	public ScrollPane scrollPane; // để chứa gridPane, hiển thị scroll tự động
	
	List<Script> scenario;
	
	public void initialize(int width) {
		gridPane = new GridPane();
		scenario = new ArrayList<Script>();
		
		Script firstText = new Script();
		firstText.initialize(width - COL_WIDTH_1);
		firstText.scenario = this;
		firstText.showButtonFirst(true);
		firstText.showButtonLast(true);
		gridPane.addRow(0, firstText.mainPane);
		
		scenario.add(firstText);
		
		scrollPane = new ScrollPane();
	    scrollPane.setContent(gridPane);
	    
	    gridPane.setGridLinesVisible(true);
	}
	
	/*
	 * Hàm thiết lập lại các button của toàn bộ Text tùy theo vị trí (last/first)
	 */
	public void validateAllButtons() {
		for (int i=0; i<scenario.size(); i++) {
			Script oneT = scenario.get(i);
			if (i == 0) {
				oneT.showButtonFirst(true);
			} else {
				oneT.showButtonFirst(false);
			}
			if (i == scenario.size()-1) {
				oneT.showButtonLast(true);
			} else {
				oneT.showButtonLast(false);
			}
		}
	}
	public void addNewText() {
		Script oneText = new Script();
		oneText.initialize((int) scenario.get(0).text.getWidth());
		oneText.scenario = this;
		gridPane.addRow(scenario.size(), oneText.mainPane);
		scenario.add(oneText);
		oneText.text.requestFocus();
		this.validateAllButtons();
		this.debug();
	}
	
	/*
	 * Hàm thêm thành phần Text mới vào phía trước thành phần chứa Button b (được click)
	 */
	public void insertText(Button b) {
		int index = -1; // sẽ chứa index đến thành phần text chứa button b
		
		for (int i=0; i<scenario.size(); i++) {
			Script t = scenario.get(i);
			if (t.insertBtn == b) {
				index = i;
				System.out.println("Chèn text... tại vị trí #" + index);
				break;
			}
		}
		if (index < 0) return; // không tìm thấy thành phần text cần insert
		
		Script oneText = new Script();
		oneText.initialize((int) scenario.get(0).text.getWidth());
		oneText.scenario = this;
		scenario.add(index, oneText);
				
		for (int i=index+1; i<scenario.size(); i++) {
			Script t = scenario.get(i);
			gridPane.setRowIndex(t.mainPane, gridPane.getRowIndex(t.mainPane) + 1);
		}
		gridPane.addRow(index, oneText.mainPane);
		oneText.text.requestFocus();
		this.validateAllButtons();
		this.debug();
	}

	/*
	 * Hàm xóa thành phần text trên scenbario tương ứng với button b được click
	 */
	public void deleteText(Button b) {
		int index = -1; // sẽ chứa index đến thành phần text chứa button b
		Script oneT = null;
		for (int i=0; i<scenario.size(); i++) {
			Script t = scenario.get(i);
			if (t.deleteBtn == b) {
				index = i;
				oneT = scenario.get(index);
				scenario.remove(index); 				
				break;
			}
		}
		if (index < 0) return; // không tìm thấy thành phần text cần xóa
		
		System.out.println("deleText() #" + index);
		gridPane.getChildren().remove(oneT.mainPane);
		for (int i=index; i<scenario.size(); i++) {
			Script t = scenario.get(i);
			gridPane.setRowIndex(t.mainPane, gridPane.getRowIndex(t.mainPane) - 1);
		}
		this.validateAllButtons();
	    this.debug();
	}
	
	/*
	 * Hàm di chuyển text chứa Button b lên trên một hàng
	 */
	public void moveTextUp(Button b) {
		int index = -1; // sẽ chứa index đến thành phần text chứa button b
		
		for (int i=0; i<scenario.size(); i++) {
			Script t = scenario.get(i);
			if (t.moveUpBtn == b) {
				index = i;				
				break;
			}
		}
		if (index < 0) return; // không tìm thấy thành phần text cần xóa
		
		System.out.println("moveTextUp() #" + index);
		Script oneT = scenario.get(index);
		scenario.remove(index);
		scenario.add(index-1, oneT);
		
		int i = gridPane.getRowIndex(oneT.mainPane);
		gridPane.setRowIndex(oneT.mainPane, i-1);
		Script t = scenario.get(index);
		i = gridPane.getRowIndex(t.mainPane);
		gridPane.setRowIndex(t.mainPane, i+1);
		
		this.validateAllButtons();
		oneT.text.requestFocus();
		this.debug();
	}
	
	/*
	 * Hàm di chuyển text chứa Button b xuống dưới một hàng
	 */
	public void moveTextDown(Button b) {
		int index = -1; // sẽ chứa index đến thành phần text chứa button b
		
		for (int i=0; i<scenario.size(); i++) {
			Script t = scenario.get(i);
			if (t.moveDownBtn == b) {
				index = i;				
				break;
			}
		}
		if (index < 0) return; // không tìm thấy thành phần text cần xóa
		
		System.out.println("moveTextDown() #" + index);
		Script oneT = scenario.get(index);
		scenario.remove(index);
		scenario.add(index+1, oneT);
		
		int i = gridPane.getRowIndex(oneT.mainPane);
		gridPane.setRowIndex(oneT.mainPane, i+1);
		Script t = scenario.get(index);
		i = gridPane.getRowIndex(t.mainPane);
		gridPane.setRowIndex(t.mainPane, i-1);
		
		this.validateAllButtons();
		oneT.text.requestFocus();
		this.debug();
	}
	
	public void resize(int width) {
		for (int i=0; i<scenario.size(); i++) {
		    Script t = scenario.get(i);
		    t.text.setPrefWidth(width - COL_WIDTH_1);
		}
	}
	
	public void debug() {
		System.out.print("Scenario:");
		for (int i = 0; i<scenario.size(); i++) {
			Script oneT = scenario.get(i);
			System.out.print(" [#" + i + ": " + oneT.text.getText() + "]");
		}
		/*
		System.out.print("\nGridPane:");
		for (Node child : gridPane.getChildren()) {
	        Integer rowIndex = GridPane.getRowIndex(child);
	        System.out.print(" [#" + rowIndex.intValue() + "]");
	    }
	    */
	}
}
