package bg.sofia.uni.fmi.mjt.spotify.model.song;

import bg.sofia.uni.fmi.mjt.spotify.util.SongByPlaysComparator;
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
    private static final int PLAYLIST_INDEX = 0;
    private static final int SONG_INDEX = 1;

    private final Map<String, Song> allSongs;
    private final Map<String, Playlist> allPlaylists;

    public SongManager() {
        allSongs = new HashMap<>();
        allPlaylists = new HashMap<>();
    }

    public void matchPlaylistToSong(String line) {
        String[] tokens = line.split(",");

        String playlistName = tokens[PLAYLIST_INDEX];
        String songName = tokens[SONG_INDEX];

        Song song = allSongs.get(songName);
        allPlaylists.get(playlistName).addSong(song);
    }

    public void createSong(Song song) {
        if (allSongs.containsKey(song.name())) {
            throw new SongAlreadyExistsException(song + " already exists");
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

    public Playlist createPlaylist(String name, String userEmail) {
        if (allPlaylists.containsKey(name)) {
            throw new PlaylistAlreadyExistsException("Playlist " + name + " already exists");
        }

        Playlist playlistToAdd = new Playlist(name, userEmail);
        allPlaylists.put(name, playlistToAdd);

        return playlistToAdd;
    }

    public List<String> search(String[] keywords) {
        return allSongs.values()
            .stream()
            .filter(song ->
                Arrays.stream(keywords)
                    .anyMatch(keyword ->
                        song.name().toLowerCase().contains(keyword.toLowerCase())))
            .map(Song::toString)
            .toList();
    }

    public List<String> top(int topSongs) {
        return allSongs.values()
            .stream()
            .sorted(new SongByPlaysComparator().reversed())
            .limit(topSongs)
            .map(Song::toString)
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

    public List<String> showPlaylist(String playlistName) {
        if (!allPlaylists.containsKey(playlistName)) {
            throw new PlaylistNotFoundException("Playlist " + playlistName + " doesn't exist");
        }

        return allPlaylists.get(playlistName).showPlaylist();
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
