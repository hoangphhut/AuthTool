package AuthTool;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
		
		saveOption();
	}
	@FXML
    void CancelBtnClicked(ActionEvent event) {
		Studio.pOptionDialogStage.close();		
	}
	
	public void saveOption() {
		Path path = FileSystems.getDefault().getPath("").toAbsolutePath();		
		System.out.println("Working dir: " + path.toString());
		String oF = path.toString() + "/" + "option.json";
		try {
			String s = "{\"POWERPOINT\" : \"" + Studio.POWERPOINT.replace("\\", "\\\\") + "\"}";
			Files.write(Paths.get(oF), s.getBytes());
	     } catch (Exception e) {
	    	 e.printStackTrace();
	     }
	}
	
	public void loadOption() {
		Path path = FileSystems.getDefault().getPath("").toAbsolutePath();		
		System.out.println("Working dir: " + path.toString());
		String oF = path.toString() + "/" + "option.json";
		try {
	    	 String oS = Files.readString(Path.of(oF));
	    	 JsonParser parser = new JsonParser();
	    	 JsonObject jObj = parser.parse(oS).getAsJsonObject();
	    	 
	    	 Studio.POWERPOINT = jObj.get("POWERPOINT").getAsString().trim();
	    	 System.out.println("POWERPOINT: " + Studio.POWERPOINT);
	     } catch (Exception e) {
	    	 e.printStackTrace();
	     }
	}
}

