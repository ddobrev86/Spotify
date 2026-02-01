package bg.sofia.uni.fmi.mjt.spotify.model.song;

public class Song {
    private static final int NAME_INDEX = 0;
    private static final int ARTIST_INDEX = 1;
    private static final int URL_INDEX = 2;
    private static final int TOTAL_PLAYS_INDEX = 3;

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

        String name = tokens[NAME_INDEX];
        String artist = tokens[ARTIST_INDEX];
        String url = tokens[URL_INDEX];
        int totalPlays = Integer.parseInt(tokens[TOTAL_PLAYS_INDEX]);

        return new Song(name, artist, url, totalPlays);
    }

    @SuppressWarnings("checkstyle:TodoComment")
    public void play() {
        totalPlays++;
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
        return "Song " + this.name + " by " + artist;
    }
}
