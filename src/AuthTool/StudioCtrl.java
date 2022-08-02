package AuthTool;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class StudioCtrl {
	@FXML
    public Button AddBtn;
	@FXML
    public GridPane gridPane;
	
	static int n = 0;
	static List<Parent> lines = new ArrayList<Parent>();
	@FXML
	public void AddBtnClicked() {
		System.out.println("AddBtnClicked()");
		try {
			Parent root = FXMLLoader.load(getClass().getResource("../Paragraph.fxml"));
			lines.add(root);
			System.out.println("gridPane.getRowCount(): " + gridPane.getRowCount());
			//gridPane.add(root, 0, StudioCtrl.n++);
			gridPane.addRow(n++, root);
			//gridPane.setPrefHeight(root.)
			System.out.println("gridPane.getRowCount(): " + gridPane.getRowCount());
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
}
