package bg.sofia.uni.fmi.mjt.spotify.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MusicPlayer {
    private final ExecutorService player = Executors.newSingleThreadExecutor();
    private Future<?> currentTask;
    private PlayedSong currentSong = null;

    public synchronized void play(String url) {
        if (currentTask != null && !currentTask.isDone()) {
            currentSong.stop();
        }

        currentSong = new PlayedSong(url);
        currentTask = player.submit(currentSong);
    }

    public void stop() {
        if (currentSong != null) {
            currentSong.stop();
            currentSong = null;
            currentTask = null;
        }
    }

    public void disconnect() {
        stop();
        player.shutdown();
    }
}
