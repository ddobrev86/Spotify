package bg.sofia.uni.fmi.mjt.spotify.model.song;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.song.InvalidPlaylistOwnershipException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.song.SongAlreadyExistsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Playlist {
    private final String name;
    private Map<String, Song> songs;
    private final String ownerEmail;
    private boolean isPrivate;

    public Playlist(String name, String userEmail) {
        this.name = name;
        songs = new HashMap<>();
        this.ownerEmail = userEmail;
        this.isPrivate = false;
    }

    public void addSong(Song song, String userEmail) {
        if (isPrivate && !this.ownerEmail.equals(userEmail)) {
            throw new InvalidPlaylistOwnershipException("You don't own this private playlist and cannot add a song");
        }

        if (songs.containsKey(song.name())) {
            throw new SongAlreadyExistsException(song.toString() + "is already in playlist " + this.name);
        }
        songs.put(song.name(), song);
    }

    public List<String> showPlaylist() {
        return songs.values()
            .stream()
            .map(Song::toString)
            .toList();
    }

    public List<String> showSongsNamesOnly() {
        return songs.values()
            .stream()
            .map(Song::name)
            .toList();
    }

    public String name() {
        return this.name;
    }

    public String userEmail() {
        return this.ownerEmail;
    }

    public boolean isPrivate() {
        return this.isPrivate;
    }

    public int songCount() {
        return this.songs.size();
    }

    public void togglePrivacy(String userEmail) {
        if (!this.ownerEmail.equals(userEmail)) {
            throw new InvalidPlaylistOwnershipException("You don't own this playlist and cannot change it's privacy");
        }

        this.isPrivate = !this.isPrivate;
    }
}
