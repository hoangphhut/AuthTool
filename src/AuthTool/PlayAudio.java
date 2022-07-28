package AuthTool;

import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class PlayAudio extends Thread {
	String audio_file;
	MediaPlayer mediaPlayer;
	
	public void run() {
		System.out.println("Play " + audio_file + " in new thread: " + this.toString());
		if (audio_file == null) return;
		System.out.println("--> create media object");
		Media media = new Media(new File(audio_file).toURI().toString());
		System.out.println("--> create media player");
        mediaPlayer = new MediaPlayer(media);
        Studio.PLAYING = true;
        mediaPlayer.setAutoPlay(true);
        System.out.println("--> start play");
        
        
        mediaPlayer.setOnEndOfMedia(() -> {
        	System.out.println("Kết thúc play một paragraph");
        	Studio.PLAYING = false;
        });
        
        mediaPlayer.play();
        System.out.println("--> after invoking play...");
	}
}
