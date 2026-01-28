package bg.sofia.uni.fmi.mjt.spotify.exceptions;

public class SongAlreadyExistsException extends RuntimeException {
    public SongAlreadyExistsException(String message) {
        super(message);
    }

    public SongAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
