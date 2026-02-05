package bg.sofia.uni.fmi.mjt.spotify.exceptions.song;

public class SongNotFoundException extends RuntimeException {
    public SongNotFoundException(String message) {
        super(message);
    }
}
