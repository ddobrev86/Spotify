package bg.sofia.uni.fmi.mjt.spotify.log.types;

public record UserErrorLog(String user, String errorType, String action, String message) implements Log {
    @Override
    public String toString() {
        return "[" + user + "] " + errorType + message;
    }
}
