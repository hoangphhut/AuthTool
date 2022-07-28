package AuthTool;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class ActionDialogController {
	@FXML
    public TextField ActionDescTfl;
	@FXML
    public ChoiceBox<String> ActionCategoryCbx;
	@FXML
    public ChoiceBox<String> ActionCbx;
	@FXML
    public TextField ActionParaTfl;
	@FXML
    public ChoiceBox<String> DelayCbx;
	@FXML
    public Button DeleteBtn;
	@FXML
    public Button CancelBtn;
	@FXML
    public Button OKBtn;
	
	public int WorkingMode; // =0: Add New, =1: Edit	
	Paragraph paragraph;
	PreAction oldAction; // trường hợp edit, đây là action cần sửa
	
	public void initialize(Paragraph p, PreAction oA) {
		paragraph = p;
		oldAction = oA;
		if (oldAction == null) oldAction = new PreAction();
		
		ObservableList<String> actionCat = FXCollections.observableArrayList(
				PreAction.KEY + " Thao tác bàn phím", 
				PreAction.MOUSE + " Thao tác chuột",
				PreAction.APP + " Ứng dụng trình diễn");
		ActionCategoryCbx.setItems(actionCat);
		
		ObservableList<String> delayList = FXCollections.observableArrayList("0", "100", "200", "500", "1000", "2000", "3000", "5000");
		DelayCbx.setItems(delayList);
		
		if (WorkingMode == 0) { // Add new
			DeleteBtn.setVisible(false);
			ActionCategoryCbx.getSelectionModel().select(0);
			ActionCbx.getSelectionModel().select(0);
			ActionParaTfl.setText("");
			DelayCbx.getSelectionModel().select(0);
		} else { // Edit
			DeleteBtn.setVisible(true);
			if (oldAction != null) {
				ActionCategoryCbx.getSelectionModel().select(oldAction.ActionGroup);
				ActionCbx.getSelectionModel().select(oldAction.Action);
				ActionParaTfl.setText(oldAction.ActionPara);
				DelayCbx.getSelectionModel().select("" + oldAction.Delay);
			}
		}
	}
	
	@FXML
	public void CategoryChange() {
		String aCat = ActionCategoryCbx.getSelectionModel().getSelectedItem();

		if (aCat.startsWith(PreAction.APP)) {
			ObservableList<String> actionCat = FXCollections.observableArrayList(
					PreAction.PPT_OPEN + " <file trình chiếu>", 
					PreAction.PPT_CLOSE);
			ActionCbx.setItems(actionCat);
			ActionCbx.getSelectionModel().select(0);
			
		} else if (aCat.startsWith(PreAction.KEY)) {
			ObservableList<String> actionCat = FXCollections.observableArrayList(
					PreAction.RIGHT,
					PreAction.LEFT,
					PreAction.PGUP,
					PreAction.PGDN,
					PreAction.ENTER,
					PreAction.F5, 				 
					PreAction.SHIFT_F5);
			ActionCbx.setItems(actionCat);
			ActionCbx.getSelectionModel().select(0);
		} else if (aCat.startsWith(PreAction.MOUSE)) {
			ObservableList<String> actionCat = FXCollections.observableArrayList(
					PreAction.MOUSE_LEFT, 
					PreAction.MOUSE_RIGHT);
			ActionCbx.setItems(actionCat);
			ActionCbx.getSelectionModel().select(0);
		}
	}
	
	@FXML
	public void ActionChange() {
		PreAction p = this.saveData();
		ActionDescTfl.setText(p.toString());
	}
	
	@FXML
	public void ActionParaChange() {
		ActionChange();
	}
	
	@FXML
	public void DelayChange() {
		ActionChange();
	}
	
	@FXML
    void OKBtnClicked(ActionEvent event) {
		PreAction p = this.saveData();
		if (this.WorkingMode == 0) { // add new mode
			this.paragraph.all_actions.add(p);			
		} else { // edit mode
			oldAction.copy(p);
		}
		
		paragraph.validateActionUI();
		Studio.pActionDialogStage.close();
		if (this.WorkingMode == 0) { // add new mode
			paragraph.actionEditBtn.get(paragraph.actionEditBtn.size()-1).requestFocus();			
		} else { // edit mode
			int i = paragraph.all_actions.indexOf(oldAction);
			paragraph.actionEditBtn.get(i).requestFocus();
		}
		
	}
	@FXML
    void CancelBtnClicked(ActionEvent event) {
		Studio.pActionDialogStage.close();		
	}
	@FXML
    void DeleteBtnClicked(ActionEvent event) {
		paragraph.all_actions.remove(oldAction);			
		paragraph.validateActionUI();
		Studio.pActionDialogStage.close();
		paragraph.actionEditBtn.get(paragraph.actionEditBtn.size()-1).requestFocus();
	}
	public PreAction saveData() {
		PreAction p = new PreAction();
		//p.ActionPara.replace("\\", "\\\\");
		p.ActionGroup = ActionCategoryCbx.getSelectionModel().getSelectedItem();
		if (p.ActionGroup == null) p.ActionGroup = "";
		if (p.ActionGroup.startsWith(PreAction.APP)) p.ActionGroup = PreAction.APP;
		if (p.ActionGroup.startsWith(PreAction.KEY)) p.ActionGroup = PreAction.KEY;
		if (p.ActionGroup.startsWith(PreAction.MOUSE)) p.ActionGroup = PreAction.MOUSE;
		
		p.Action = ActionCbx.getSelectionModel().getSelectedItem();
		if (p.Action == null) p.Action = "";
		if (p.Action.startsWith(PreAction.PPT_OPEN)) p.Action = PreAction.PPT_OPEN;
		p.ActionPara = ActionParaTfl.getText();
		try {
			p.Delay = Integer.parseInt(DelayCbx.getSelectionModel().getSelectedItem());
		} catch (Exception e) {
			p.Delay = 0;
		}
		
		return p;		
	}
}

