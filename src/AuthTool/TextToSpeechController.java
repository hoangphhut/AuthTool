package AuthTool;

import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TextToSpeechController {
	@FXML
    public TextArea TextTa;
	@FXML
    public TextField AuthorizationTf;
	@FXML
    public TextField app_idTf;
	@FXML
    public TextField callback_urlTf;
	@FXML
    public TextField voice_codeTf;
	@FXML
    public TextField speed_rateTf;
	@FXML
    public TextArea LogTa;
	@FXML
    public Button TextToSpeechBt;
	@FXML
    public CheckBox ProgressCk;
	@FXML
    public Button OpenVoiceBt;
	
	@FXML
    void TextToSpeechBtnClicked(ActionEvent event) {
        System.out.println("TextToSpeechBtn...");
        Vbee vbee = new Vbee();
        vbee.working_dir = "D:/working/data";
        vbee.auth = AuthorizationTf.getText();
        vbee.app_id = app_idTf.getText();
        vbee.callback_url = callback_urlTf.getText();
        vbee.voice_code = voice_codeTf.getText();
        vbee.speed_rate = speed_rateTf.getText();
        vbee.text = TextTa.getText();
        vbee.ProgressCk = ProgressCk;
        vbee.ProgressLog = LogTa;
        vbee.text_to_speech();
    }
	
	
	private void closeStage(ActionEvent event) {
        Node  source = (Node)  event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
