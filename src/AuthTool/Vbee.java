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
    
    public CheckBox ProgressCk; // để chọn có theo dõi tiến độ xử lý hay không
    public TextArea ProgressLog; // để hiện thị thông tin tiến độ
    
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
    	    httpPost.setHeader("Content-Type", "application/json");
    	    httpPost.setHeader("Authorization", "Bearer " + auth);
    	    String body = "{\n"
    	    		+ "    \"app_id\": \"" + app_id + "\",\n"
    	    		+ "    \"callbackUrl\": \"" + callback_url + "\",\n"
    	    		+ "    \"input_text\": \"" + text +"\",\n"
    	    		+ "    \"voice_code\": \"" + voice_code + "\",\n"
    	    		+ "    \"audio_type\": \"mp3\",\n"
    	    		+ "    \"bitrate\": 128,\r\n"
    	    		+ "    \"speed_rate\": " + speed_rate + "\n}";
    	    StringEntity entity = new StringEntity(body);
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
    		log("-> audio_link: " + mp3_url);
    		
    		InputStream in = new URL(mp3_url).openStream();
    		mp3_file = new Date().getTime() + ".mp3";
    		Files.copy(in, Paths.get(working_dir + "/" + mp3_file), StandardCopyOption.REPLACE_EXISTING);
    		log("-> mp3 file: " + mp3_file);
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
