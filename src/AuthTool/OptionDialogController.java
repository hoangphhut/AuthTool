package AuthTool;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
		
		Studio.saveOption();
	}
	@FXML
    void CancelBtnClicked(ActionEvent event) {
		Studio.pOptionDialogStage.close();		
	}	
}

