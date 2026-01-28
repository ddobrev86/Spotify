package bg.sofia.uni.fmi.mjt.spotify.model;

import java.util.HashMap;
import java.util.Map;

public class Playlist {
    private final String name;
    private Map<String, Song> songs;
    private final String userEmail;
    private boolean isPrivate;

    public Playlist(String name, String userEmail) {
        this.name = name;
        songs = new HashMap<>();
        this.userEmail = userEmail;
        this.isPrivate = false;
    }

    public void addSong(Song song) {
        songs.put(song.name(), song);
    }

    public String name() {
        return this.name;
    }

    public String userEmail() {
        return this.userEmail;
    }

    public void togglePrivacy() {
        this.isPrivate = !this.isPrivate;
    }
}
