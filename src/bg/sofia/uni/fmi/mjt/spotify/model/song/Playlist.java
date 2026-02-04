package bg.sofia.uni.fmi.mjt.spotify.model.song;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.song.InvalidPlaylistOwnershipException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.song.SongAlreadyExistsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Playlist {
    private static final int ARGUMENT_COUNT = 2;

    private static final int NAME_INDEX = 0;
    private static final int OWNER_INDEX = 1;

    private final String name;
    private final Map<String, Song> songs;
    private final String ownerEmail;

    public Playlist(String name, String userEmail) {
        this.name = name;
        songs = new HashMap<>();
        this.ownerEmail = userEmail;
    }

    public static Playlist of(String line) {
        String[] tokens = line.split(",");
        if (tokens.length != ARGUMENT_COUNT) {
            throw new IllegalArgumentException("Invalid input for playlist");
        }

        String name = tokens[NAME_INDEX].strip();
        String owner = tokens[OWNER_INDEX].strip();

        return new Playlist(name, owner);
    }

    /**
     * Intentionally package-private: this method is only used during the initial setup
     * from the database to fill the corresponding playlist with songs
     */
    void addSong(Song song) {
        songs.put(song.name(), song);
    }

    public void addSong(Song song, String userEmail) {
        if (!this.ownerEmail.equals(userEmail)) {
            throw new InvalidPlaylistOwnershipException("You don't own this playlist and cannot add a song");
        }

        if (songs.containsKey(song.name())) {
            throw new SongAlreadyExistsException(song + "is already in playlist " + this.name);
        }
        songs.put(song.name(), song);
    }

    public List<String> showPlaylist() {
        return songs.values()
            .stream()
            .map(Song::toString)
            .toList();
    }

    public String name() {
        return this.name;
    }

    public String userEmail() {
        return this.ownerEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Playlist playlist = (Playlist) o;
        return Objects.equals(name, playlist.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
