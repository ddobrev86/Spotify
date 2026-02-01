package bg.sofia.uni.fmi.mjt.spotify.log.logger;

import bg.sofia.uni.fmi.mjt.spotify.model.song.Playlist;

import java.io.IOException;
import java.util.List;

public class PlaylistLogger extends Logger<Playlist> {
    public PlaylistLogger(String fileName) throws IOException {
        super(fileName);
    }

    @Override
    public void log(Playlist toBeLogged) throws IOException {
        int privacy = toBeLogged.isPrivate() ? 1 : 0;
        String header = toBeLogged.name() + " " + toBeLogged.userEmail() + " " + privacy + " " + toBeLogged.songCount();

        this.writer.write(header);
        this.writer.newLine();

        List<String> songs = toBeLogged.showSongsNamesOnly();

        for (String song : songs) {
            this.writer.write(song);
            this.writer.newLine();
        }
    }
}
