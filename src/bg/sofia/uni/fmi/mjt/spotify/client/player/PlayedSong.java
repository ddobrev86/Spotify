package bg.sofia.uni.fmi.mjt.spotify.client.player;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class PlayedSong implements Runnable {
    private static final int BUFFER_SIZE = 1024;
    private final String url;
    private boolean isPlaying;

    public PlayedSong(String url) {
        this.url = url;
        this.isPlaying = true;
    }

    @Override
    public void run() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(this.url));
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(info);
            dataLine.open();
            dataLine.start();

            byte[] bufferBytes = new byte[BUFFER_SIZE];
            int readBytes;
            while (isPlaying && (readBytes = audioInputStream.read(bufferBytes)) != -1) {
                dataLine.write(bufferBytes, 0, readBytes);
            }

            dataLine.drain();
            dataLine.stop();
            dataLine.close();

        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println("Could not play song");
        }
        isPlaying = false;
    }

    public void stop() {
        this.isPlaying = false;
    }
}
