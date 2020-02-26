package game2D;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Sound extends Thread {

	String filename;	// The name of the file to play
	boolean finished;	// A flag showing that the thread has finished
	
	public Sound(String fname) {
		filename = fname;
		finished = false;
	}

	/**
	 * run will play the actual sound but you should not call it directly.
	 * You need to call the 'start' method of your sound object (inherited
	 * from Thread, you do not need to declare your own). 'run' will
	 * eventually be called by 'start' when it has been scheduled by
	 * the process scheduler.
	 */
	public void run() {
		try {
			File file = new File(filename);
			AudioInputStream stream = AudioSystem.getAudioInputStream(file);
			AudioFormat format = stream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();
			Thread.sleep(100);
			while (clip.isRunning()) {
				Thread.sleep(100);
			}
			clip.close();
		} catch (Exception e) {
		}
		finished = true;

	}

    public void playTheme() throws IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("sounds/theme.wav"));
        Clip clip = AudioSystem.getClip();
        clip.open(inputStream);
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(-5.0f); // Reduce volume by 10 decibels.
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        Thread.sleep(10000); // looping as long as this thread is alive
    }

    public void echo(String fname) {
        String[] arguments = {fname};
        EchoSamplesPlayer.main(arguments);
    }
}

