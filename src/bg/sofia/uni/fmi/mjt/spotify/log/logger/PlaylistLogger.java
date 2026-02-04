package bg.sofia.uni.fmi.mjt.spotify.log.logger;

import bg.sofia.uni.fmi.mjt.spotify.model.song.Playlist;

import java.io.File;
import java.io.IOException;

public class PlaylistLogger extends Logger<Playlist> {
    public PlaylistLogger(File file, boolean append) throws IOException {
        super(file, append);
    }

    @Override
    public void log(Playlist toBeLogged) throws IOException {
        String header = toBeLogged.name() + "," + toBeLogged.userEmail() + System.lineSeparator();

        this.writer.write(header);
        this.writer.flush();
    }
}
