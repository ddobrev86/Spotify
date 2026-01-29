package bg.sofia.uni.fmi.mjt.spotify.model.song;

import bg.sofia.uni.fmi.mjt.spotify.SongByPlaysComparator;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.song.PlaylistAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.song.PlaylistNotFoundException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.song.SongAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.song.SongNotFoundException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongManager {
    private static final String EMPTY_PLAYLIST_MESSAGE = "Playlist has no songs";

    private Map<String, Song> allSongs;
    private Map<String, Playlist> allPlaylists;

    public SongManager() {
        allSongs = new HashMap<>();
        allPlaylists = new HashMap<>();
    }

    public void createSong(Song song) {
        if (allSongs.containsKey(song.name())) {
            throw new SongAlreadyExistsException(song.toString() + " already exists");
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

    public void createPlaylist(String name, String userEmail) {
        if (allPlaylists.containsKey(name)) {
            throw new PlaylistAlreadyExistsException("Playlist " + name + " already exists");
        }

        Playlist playlistToAdd = new Playlist(name, userEmail);
        allPlaylists.put(name, playlistToAdd);
    }

    public List<Song> search(String[] keywords) {
        return allSongs.values()
            .stream()
            .filter(song ->
                Arrays.stream(keywords)
                    .anyMatch(keyword ->
                        song.name().toLowerCase().contains(keyword.toLowerCase())))
            .toList();
    }

    public List<Song> top(int topSongs) {
        return allSongs.values()
            .stream()
            .sorted(new SongByPlaysComparator().reversed())
            .limit(topSongs)
            .toList();
    }

    public void addSongToPlaylist(String playlistName, String songName, String userEmail) {
        if (!allPlaylists.containsKey(playlistName)) {
            throw new PlaylistNotFoundException("Playlist " + playlistName + " doesn't exist");
        }

        if (!allSongs.containsKey(songName)) {
            throw new SongNotFoundException("Song " + songName + " doesn't exist");
        }

        Song songToAdd = allSongs.get(songName);
        allPlaylists.get(playlistName).addSong(songToAdd, userEmail);
    }

    public String showPlaylist(String playlistName) {
        if (!allPlaylists.containsKey(playlistName)) {
            throw new PlaylistNotFoundException("Playlist " + playlistName + " doesn't exist");
        }

        List<String> result = allPlaylists.get(playlistName).showPlaylist();
        if (result.isEmpty()) {
            return EMPTY_PLAYLIST_MESSAGE;
        }

        return String.join(System.lineSeparator(), result);
    }

    public void playSong(String songName) {
        if (!allSongs.containsKey(songName)) {
            throw new SongNotFoundException("Song " + songName + " doesn't exist");
        }

        allSongs.get(songName).play();
    }

    public void togglePrivacy(String playlistName, String userEmail) {
        if (!allPlaylists.containsKey(playlistName)) {
            throw new PlaylistNotFoundException("Playlist " + playlistName + " doesn't exist");
        }

        allPlaylists.get(playlistName).togglePrivacy(userEmail);
    }
}
