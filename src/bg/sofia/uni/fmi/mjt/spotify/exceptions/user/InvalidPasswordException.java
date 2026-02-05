package bg.sofia.uni.fmi.mjt.spotify.exceptions.user;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
