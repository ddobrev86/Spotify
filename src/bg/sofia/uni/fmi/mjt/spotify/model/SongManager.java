package bg.sofia.uni.fmi.mjt.spotify.model;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlaylistAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.SongAlreadyExistsException;

import java.util.HashMap;
import java.util.Map;

public class SongManager {
    private Map<String, Song> allSongs;
    private Map<String, Playlist> allPlaylists;

    public SongManager() {
        allSongs = new HashMap<>();
        allPlaylists = new HashMap<>();
    }

    public void createSong(Song song) {
        if (allSongs.containsKey(song.name())) {
            throw new SongAlreadyExistsException("Song " + song.name() + " by " + song.artist() + " already exists");
        }

        allSongs.put(song.name(), song);
    }

    public void createSong(String name, String artist, String url) {
        if (allSongs.containsKey(name)) {
            throw new SongAlreadyExistsException("Song " + name + " by " + artist + " already exists");
        }

        Song songToAdd = new Song(name, artist, url);
        allSongs.put(name, songToAdd);
    }

    public void createPlaylist(Playlist playlist) {
        if (allPlaylists.containsKey(playlist.name())) {
            throw new PlaylistAlreadyExistsException("Playlist " + playlist.name() + " already exists");
        }

        allPlaylists.put(playlist.name(), playlist);
    }

    public void createPlaylist(String name, String artist, String url) {
        if (allSongs.containsKey(name)) {
            throw new SongAlreadyExistsException("Song " + name + " by " + artist + " already exists");
        }

        Song songToAdd = new Song(name, artist, url);
        allSongs.put(name, songToAdd);
    }
}
