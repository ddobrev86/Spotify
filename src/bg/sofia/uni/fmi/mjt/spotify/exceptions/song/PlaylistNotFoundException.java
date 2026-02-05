package bg.sofia.uni.fmi.mjt.spotify.exceptions.song;

public class PlaylistNotFoundException extends RuntimeException {
    public PlaylistNotFoundException(String message) {
        super(message);
    }
}
