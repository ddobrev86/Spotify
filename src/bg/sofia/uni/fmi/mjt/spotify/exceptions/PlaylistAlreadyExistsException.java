package bg.sofia.uni.fmi.mjt.spotify.exceptions;

public class PlaylistAlreadyExistsException extends RuntimeException {
    public PlaylistAlreadyExistsException(String message) {
        super(message);
    }

    public PlaylistAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
