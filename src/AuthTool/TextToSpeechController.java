package AuthTool;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TextToSpeechController {
//	@FXML
//	public Tab AudioFileTab;
	@FXML
    public Label AudioFileLb;
	@FXML
    public Button SelectFileBt;
	@FXML
    public Button PlayBt;
	@FXML
    public ImageView PlayingImg;
	
	@FXML
    public TextArea TextTa;
	@FXML
    public TextField AuthorizationTf;
	@FXML
    public TextField app_idTf;
	@FXML
    public TextField callback_urlTf;
	@FXML
    public ChoiceBox<String> voice_codeCbx;
	@FXML
    public ChoiceBox<String> speed_rateCbx;	
	@FXML
    public TextArea LogTa;
	@FXML
    public Button TextToSpeechBt;
	@FXML
    public CheckBox ProgressCk;
	@FXML
    public Button OpenVoiceBt;
	
	public Paragraph paragraph; //script mà Vbee này đang phục vụ
	
	/*
	 * Hàm khởi tạo Controller để xử lý một Script
	 */
	public void initToShow(Paragraph p) {
		paragraph = p;
		TextTa.setText(p.text);
		this.updateFileTab();
		
		ObservableList<String> vbee_voice_codes = FXCollections.observableArrayList(
				"hn_female_ngochuyen_full_48k-fhg", 
				"hn_male_phuthang_news65dt_44k-fhg",
				"hn_male_thanhlong_talk_48k-fhg",
				"hn_female_maiphuong_vdts_48k-fhg",
				"hn_male_manhdung_news_48k-fhg",
				"sg_female_tuongvy_call_44k-fhg",
				"sg_female_lantrinh_vdts_48k-fhg",
				"sg_male_trungkien_vdts_48k-fhg",
				"sg_male_minhhoang_full_48k-fhg",
				"sg_female_thaotrinh_full_48k-fhg",
				"hue_male_duyphuong_full_48k-fhg",
				"hue_female_huonggiang_full_48k-fhg");
		voice_codeCbx.setItems(vbee_voice_codes);
		voice_codeCbx.getSelectionModel().select(Studio.sc.vbee_voice_code);
		
		ObservableList<String> vbee_speed_rates = FXCollections.observableArrayList("0.5", "0.6", "0.7", "0.8", "0.9", "1.0", "1.1", "1.2", "1.3", "1.4", "1.5");
		speed_rateCbx.setItems(vbee_speed_rates);
		speed_rateCbx.getSelectionModel().select(Studio.sc.vbee_speed_rate);
	}
	
	@FXML
    void TextToSpeechBtnClicked(ActionEvent event) {
        System.out.println("TextToSpeechBtn...");
        this.TextToSpeechBt.setDisable(true);
        
        paragraph.vbee = new Vbee();
        paragraph.vbee.paragraph = paragraph;
        paragraph.vbee.working_dir = Studio.sc.WorkingDir;
        paragraph.vbee.auth = AuthorizationTf.getText();
        paragraph.vbee.app_id = app_idTf.getText();
        paragraph.vbee.callback_url = callback_urlTf.getText();
        paragraph.vbee.voice_code = voice_codeCbx.getSelectionModel().getSelectedItem();
        paragraph.vbee.speed_rate = speed_rateCbx.getSelectionModel().getSelectedItem();
        paragraph.vbee.text = TextTa.getText();
        paragraph.vbee.ProgressCk = ProgressCk;
        paragraph.vbee.ProgressLog = LogTa;
        
        // lưu các giá trị Vbee vào Studio.sc để sử dụng cho các text-to-speech sau này
        Studio.sc.vbee_Authorization = paragraph.vbee.auth;
        Studio.sc.vbee_app_id = paragraph.vbee.app_id;
        Studio.sc.vbee_callback_url = paragraph.vbee.callback_url;
        Studio.sc.vbee_voice_code = paragraph.vbee.voice_code;
        Studio.sc.vbee_speed_rate = paragraph.vbee.speed_rate;
                
        paragraph.vbee.text_to_speech();
    }
	
	@FXML
    void PlayBtClicked(ActionEvent event) {
        System.out.println("PlayBt... " + paragraph.audio_file);

        Studio.audioParagraph = new AudioParagraph();
        Studio.audioParagraph.initalize(this.paragraph);
        if (Studio.audioParagraph.mediaPlayer == null) return; 
        Studio.audioParagraph.manualSettingCompoleteAction = true;
        
        Studio.audioParagraph.mediaPlayer.setOnEndOfMedia(() -> {
        	System.out.println("Kết thúc play");
        	PlayingImg.setVisible(false);
        	PlayBt.setDisable(false);
        });
        
        PlayingImg.setVisible(true);
       	PlayBt.setDisable(true);
        Studio.audioParagraph.start();
	}
	@FXML
    void SelectFileBtClicked(ActionEvent event) {
        System.out.println("SelectFileBt... ");
        
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(Studio.pStage);
        if (file != null) {
            System.out.println(file.getPath() + "/" + file.getName());
            String s = file.getPath();
            s = s.replace("\\", "/");
            paragraph.audio_file = s;
            updateFileTab();
        }
	}
	@FXML
    void CompleteBtClicked(ActionEvent event) {
		System.out.println("Close... ");
		Studio.ttsDialogStage.close();
	}
	
//	@FXML
    void AudioFileTabClicked(ActionEvent event) {
		System.out.println("AudioFileTabClicked... " + event.getSource().toString());
		//this.updateFileTab();
	}
	
	/*
	 * Hàm hiển thị lại tên file audio của paragraph hiện tại. Được gọi khi có sự thay đổi về file
	 * Hàm này tham chiếu đến các trường trong DialogBox và có thể được gọi từ các thread không liên quan đến FX nên cần tham chiếu qua Studio
	 */
	public void updateFileTab() {
		System.out.println("updateFileTab() " + paragraph.audio_file);
		if (paragraph.audio_file != null) {
			String s = "File âm thanh: " + paragraph.audio_file;
			Path f = Paths.get(paragraph.audio_file);
			try {
				BasicFileAttributes attrs = Files.readAttributes(f, BasicFileAttributes.class);
				LocalDateTime localT = LocalDateTime.ofInstant(attrs.creationTime().toInstant(), ZoneId.systemDefault());
				paragraph.audio_file_info = localT.toString();
			} catch (Exception ignore) {}
			
			if (paragraph.audio_file_info != null) {
				s = s + " [" + paragraph.audio_file_info + "]";
			}
			Studio.ttsDialog.AudioFileLb.setText(s);
			Studio.ttsDialog.PlayBt.setDisable(false);
		} else {
			Studio.ttsDialog.AudioFileLb.setText("Chưa có file âm thanh");
			Studio.ttsDialog.PlayBt.setDisable(true);
		}
	}
	/*
	private void closeStage(ActionEvent event) {
        Node  source = (Node)  event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
    */
}
