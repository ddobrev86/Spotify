package bg.sofia.uni.fmi.mjt.spotify.exceptions.user;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException(String message) {
        super(message);
    }

//    public IncorrectPasswordException(String message, Throwable cause) {
//        super(message, cause);
//    }
}
