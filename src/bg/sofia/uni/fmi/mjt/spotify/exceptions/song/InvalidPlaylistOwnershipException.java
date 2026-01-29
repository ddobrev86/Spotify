package bg.sofia.uni.fmi.mjt.spotify.exceptions.song;

public class InvalidPlaylistOwnershipException extends RuntimeException {
    public InvalidPlaylistOwnershipException(String message) {
        super(message);
    }

    public InvalidPlaylistOwnershipException(String message, Throwable cause) {
        super(message, cause);
    }
}
