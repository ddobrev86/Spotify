package bg.sofia.uni.fmi.mjt.spotify.log.logger;

import bg.sofia.uni.fmi.mjt.spotify.model.song.PlaylistSongPair;

import java.io.File;
import java.io.IOException;

public class PlaylistToSongLogger extends Logger<PlaylistSongPair> {
    public PlaylistToSongLogger(File file, boolean append) throws IOException {
        super(file, append);
    }

    @Override
    public void log(PlaylistSongPair toBeLogged) throws IOException {
        this.writer.write(toBeLogged.playlistName() + "," + toBeLogged.songName() + System.lineSeparator());
        this.writer.flush();
    }
}
