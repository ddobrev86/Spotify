package bg.sofia.uni.fmi.mjt.spotify.model;

public class Song {
    private static int idCounter = 0;

    private int id;
    private String name;
    private String artist;
    private String url;
    private int totalPlays;

    public Song(String name, String artist, String url) {
        this.id = idCounter++;
        this.artist = artist;
        this.name = name;
        this.url = url;
        this.totalPlays = 0;
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

    int id() {
        return this.id;
    }
}
