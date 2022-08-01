package AuthTool;

import java.io.File;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.robot.Robot;

public class AudioParagraph extends Thread {
	public Paragraph paragraph;
	public Robot robot;
	public MediaPlayer mediaPlayer;
	
	public boolean manualSettingCompoleteAction = false; // thông báo thiết lập hàm tự động xử lý khi kết thúc audio
	public boolean stopPresentationByComplete = false;
	public boolean continueNextParagraph = false;
	
	public void initalize(Paragraph p) {
		paragraph = p;
		mediaPlayer = null;
		//System.out.println("--> create media object");
		if (paragraph.audio_file == null) return;
		File f = new File(paragraph.audio_file);
		if (f == null) return;
		try {
			Media media = new Media(f.toURI().toString());
			System.out.println("--> Tạo media player cho audio: " + media);
			mediaPlayer = new MediaPlayer(media);
		} catch (Exception e) {
			//e.printStackTrace();
			System.err.println("Lỗi tạo media object từ audio_file: " + paragraph.audio_file);
		}
	}
	
	public void run() {
		System.out.println("Play " + paragraph.audio_file + " in new thread: " + this.toString());		
        		
        if (! manualSettingCompoleteAction && mediaPlayer != null) {
        	mediaPlayer.setOnEndOfMedia(() -> {
        		System.out.println("Kết thúc play paragraph " + paragraph.toString());
        		if (continueNextParagraph) {
        			int i = Studio.sc.all_paragraph.indexOf(paragraph);
        			if (i<Studio.sc.all_paragraph.size()-1) {
        				Paragraph nextP = Studio.sc.all_paragraph.get(i+1);
        				nextP.quickShow(robot, 0, 0);
        				
        				if (nextP.audio_file != null) {
        					Studio.audioParagraph = new AudioParagraph();
        					Studio.audioParagraph.initalize(Studio.sc.all_paragraph.get(i+1));
        					Studio.audioParagraph.stopPresentationByComplete = this.stopPresentationByComplete;
        					Studio.audioParagraph.robot = this.robot;
        					Studio.audioParagraph.continueNextParagraph = this.continueNextParagraph;
        					Studio.audioParagraph.start();
        				}	
        			}
        		} else {
        			if (stopPresentationByComplete) Studio.stopPresentation(robot);
        		}	
            });
        }
        
		if (mediaPlayer != null) {
			mediaPlayer.play();
		} else {
			if (! manualSettingCompoleteAction) {
				if (continueNextParagraph) {
					int i = Studio.sc.all_paragraph.indexOf(paragraph);
        			if (i<Studio.sc.all_paragraph.size()-1) {
        				Paragraph nextP = Studio.sc.all_paragraph.get(i+1);
        				nextP.quickShow(robot, 0, 0);
        				
        				if (nextP.audio_file != null) {
        					Studio.audioParagraph = new AudioParagraph();
        					Studio.audioParagraph.initalize(Studio.sc.all_paragraph.get(i+1));
        					Studio.audioParagraph.stopPresentationByComplete = this.stopPresentationByComplete;
        					Studio.audioParagraph.robot = this.robot;
        					Studio.audioParagraph.continueNextParagraph = this.continueNextParagraph;
        					Studio.audioParagraph.start();
        				}	
        			}
				} else {
        			if (stopPresentationByComplete) {
        				Platform.runLater(()->Studio.stopPresentation(robot)); 
        				//Studio.stopPresentation(robot);
        			}
        		}
			}
		}
        System.out.println("--> after invoking play...");
	}	
}
