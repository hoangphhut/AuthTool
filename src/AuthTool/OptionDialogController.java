package AuthTool;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class OptionDialogController {
	@FXML
    public TextField PowerpointTfl;
	
	public void initialize() {
		PowerpointTfl.setText(Studio.POWERPOINT);
	}
	@FXML
    void OKBtnClicked(ActionEvent event) {
		Studio.POWERPOINT = PowerpointTfl.getText();
		Studio.pOptionDialogStage.close();
	}
	@FXML
    void CancelBtnClicked(ActionEvent event) {
		Studio.pOptionDialogStage.close();		
	}
}

