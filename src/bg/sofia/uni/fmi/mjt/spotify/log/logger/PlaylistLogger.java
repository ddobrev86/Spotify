package bg.sofia.uni.fmi.mjt.spotify.log.logger;

import bg.sofia.uni.fmi.mjt.spotify.model.song.Playlist;

import java.io.File;
import java.io.IOException;

public class PlaylistLogger extends Logger<Playlist> {
    public PlaylistLogger(File file) throws IOException {
        super(file);
    }

    @Override
    public void log(Playlist toBeLogged) throws IOException {
        int privacy = toBeLogged.isPrivate() ? 1 : 0;
        String header = toBeLogged.name() + "," + toBeLogged.userEmail() + "," + privacy + System.lineSeparator();

        this.writer.write(header);
        this.writer.flush();
    }
}
