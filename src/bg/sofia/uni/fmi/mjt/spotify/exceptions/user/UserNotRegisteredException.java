package bg.sofia.uni.fmi.mjt.spotify.exceptions.user;

public class UserNotRegisteredException extends RuntimeException {
    public UserNotRegisteredException(String message) {
        super(message);
    }
}
