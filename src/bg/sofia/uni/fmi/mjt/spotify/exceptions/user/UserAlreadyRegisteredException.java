package bg.sofia.uni.fmi.mjt.spotify.exceptions.user;

public class UserAlreadyRegisteredException extends RuntimeException {
    public UserAlreadyRegisteredException(String message) {
        super(message);
    }
}
