package bg.sofia.uni.fmi.mjt.spotify.model.song;

import java.util.Objects;

public class Song {
    private static final int ARGUMENT_COUNT = 3;

    private static final int NAME_INDEX = 0;
    private static final int ARTIST_INDEX = 1;
    private static final int URL_INDEX = 2;

    private final String name;
    private final String artist;
    private final String url;
    private int totalPlays;

    public Song(String name, String artist, String url, int totalPlays) {
        this.artist = artist;
        this.name = name;
        this.url = url;
        this.totalPlays = totalPlays;
    }

    public Song(String name, String artist, String url) {
        this.artist = artist;
        this.name = name;
        this.url = url;
        this.totalPlays = 0;
    }

    public static Song of(String line) {
        String[] tokens = line.split(",");
        if (tokens.length != ARGUMENT_COUNT) {
            throw new IllegalArgumentException("Invalid input for song");
        }

        String name = tokens[NAME_INDEX].strip();
        String artist = tokens[ARTIST_INDEX].strip();
        String url = tokens[URL_INDEX].strip();

        return new Song(name, artist, url);
    }

    public void play() {
        this.totalPlays++;
    }

    public String name() {
        return this.name;
    }

    public String artist() {
        return this.artist;
    }

    public String url() {
        return this.url;
    }

    public int totalPlays() {
        return this.totalPlays;
    }

    @Override
    public String toString() {
        return this.name + " by " + this.artist;
    }

    public String songPlays() {
        return this + ": " + totalPlays;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Song song = (Song) o;
        return Objects.equals(name, song.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
