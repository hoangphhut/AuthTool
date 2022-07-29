package AuthTool;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;

/*
 * Class xử lý text-to-speech với công nghệ Vbee
 */
public class Vbee extends Thread {
	public String text;
	public String auth; // bearer authen
    public String app_id;
    public String callback_url;
    public String voice_code;
    public String speed_rate;

    public String request_id;  
    public String status; // trạng thái đang xử lý tại Vbee
    public String working_dir; //thư mục làm việc (chứa file kết quả)
    public String mp3_file = null; //file kết quả
    public String mp3_url = null; //url chứa file kết quả (sẽ bị xóa khi hết hạn)
    
    public Paragraph paragraph = null; // Paragraph đang được Vbee xử lý
    public CheckBox ProgressCk; // để chọn có theo dõi tiến độ xử lý hay không (xuất hiện trên TTSDialog)
    public TextArea ProgressLog; // để hiện thị thông tin tiến độ (xuất hiện trên TTSDialog)
    
    public void log(String s) {
  		if (ProgressCk != null && ProgressCk.isSelected()) {
       		if (ProgressLog != null) {
       			ProgressLog.setText(ProgressLog.getText() + "\n" + s);
       			ProgressLog.positionCaret(ProgressLog.getText().length());
       		}
       	}    	
    }
    public void text_to_speech() {
        try {
        	CloseableHttpClient httpClient = HttpClients.createDefault();
    		String urlStr = "https://vbee.vn/api/v1/tts";
    		HttpPost httpPost = new HttpPost(urlStr);
    	    httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
    	    httpPost.setHeader("Authorization", "Bearer " + auth);
    	    String vbee_text = text.replace("\n", "."); //Vbee không chấp nhận ký tự xuống dòng nên thay bằng dấu chấm
    	    vbee_text = vbee_text.replace("\"", ","); //text trong nháy kép được chuyển thành dấu phẩy
    	    String body = "{"
    	    		+ "\"app_id\": \"" + app_id + "\", "
    	    		+ "\"callbackUrl\": \"" + callback_url + "\", "
    	    		+ "\"input_text\": \"" + vbee_text +"\", " //Vbee không chấp nhận ký tự xuống dòng nên thay bằng dấu chấm
    	    		+ "\"voice_code\": \"" + voice_code + "\", "
    	    		+ "\"audio_type\": \"mp3\", "
    	    		+ "\"bitrate\": 128, "
    	    		+ "\"speed_rate\": " + speed_rate + "}";
    	    StringEntity entity = new StringEntity(body, "UTF-8");
    	    log("Gửi dữ liệu lên Vbee:\n" + body);
    	    httpPost.setEntity(entity);
    	    
    	    ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                // System.out.println("- Http response status: " + status);
                if (status >= 200 && status < 300) {
                    HttpEntity response_entity = response.getEntity();
                    return response_entity != null ? EntityUtils.toString(response_entity) : null;
                } else {
                	//System.out.println("- Http unexpected response status: " + status);
                    return null;
                }
            };

    		String responseBody = httpClient.execute(httpPost, responseHandler);
    		if (responseBody == null) {
    			log("\nVbee không chấp nhận yêu cầu. Hãy kiểm tra lại nội dung văn bản gửi đi.");
    			httpClient.close();
    			return;
    		}
    		
    		log("\nVbee ghi nhận yêu cầu: " + responseBody);
    		httpClient.close();
    		
    		JsonParser parser = new JsonParser();
    		JsonObject respBodyJson = parser.parse(responseBody).getAsJsonObject();
    		request_id = respBodyJson.getAsJsonObject("result").get("request_id").getAsString();
    		paragraph.vbee_request_id = request_id; // paragraph đã được gửi lên Vbeen với mã số request_id 
    		paragraph.vbee_audio_url = null; // mp3 url cũ không còn giá trị
    		log("-> request_id: " + request_id);
    		this.start();
    		
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    public String check_progress() {
    	try {
    		CloseableHttpClient httpClient = HttpClients.createDefault();
    		String urlStr = "https://vbee.vn/api/v1/tts/" + request_id;
    		HttpGet httpGet = new HttpGet(urlStr);
    	    httpGet.setHeader("Content-Type", "application/json");
    	    httpGet.setHeader("Authorization", "Bearer " + auth);
    	    
    	    ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                //System.out.println("- Http response status: " + status);
                if (status >= 200 && status < 300) {
                    HttpEntity response_entity = response.getEntity();
                    return response_entity != null ? EntityUtils.toString(response_entity) : null;
                } else {
                	//System.out.println("- Http unexpected response status: " + status); 
                    return null;
                }
            };
            
    		String responseBody = httpClient.execute(httpGet, responseHandler);
    		//System.out.println("- got result: " + responseBody);
    		httpClient.close();
    		
    		JsonParser parser = new JsonParser();
    		JsonObject respBodyJson = parser.parse(responseBody).getAsJsonObject();
    		String status = respBodyJson.getAsJsonObject("result").get("status").getAsString();
    		log("-> status: " + status + " " + respBodyJson.getAsJsonObject("result").get("progress").getAsString() + "%");
    		
    		if ("SUCCESS".compareTo(status.toUpperCase()) != 0) return status; // request đang xử lý hoặc có lỗi (chưa hoàn thành)
    		mp3_url = respBodyJson.getAsJsonObject("result").get("audio_link").getAsString();
    		paragraph.vbee_audio_url = mp3_url;
    		log("-> audio_link: " + mp3_url);
    		
    		InputStream in = new URL(mp3_url).openStream();
    		mp3_file = new Date().getTime() + ".mp3";
    		Files.copy(in, Paths.get(working_dir + "/" + mp3_file), StandardCopyOption.REPLACE_EXISTING);
    		log("-> mp3 file: " + working_dir + "/" + mp3_file);
    		paragraph.audio_file = working_dir + "/" + mp3_file;
    		
    		if (Studio.ttsDialog != null) {
    			Platform.runLater(()->Studio.ttsDialog.updateFileTab());
    		}
    		return status;
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return "EXCEPTION";
    }
    
    public void run() {
    	int MAX_TRY = 60; // chạy trong tối đa 60 giây
    	int DELAY = 1; // nghỉ 1 giây giữa 2 lần gửi request
    	int n = 0;
    	status = "IN_PROGRESS";
    	while (n < MAX_TRY && ("IN_PROGRESS".compareTo(status) == 0)) {
    		try {
    			Thread.sleep(DELAY*1000);
    		} catch (Exception ignore) {}
    		status = check_progress();
    		n++;
    	}
    }
}
