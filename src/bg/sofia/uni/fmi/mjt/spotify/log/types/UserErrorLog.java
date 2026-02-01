package bg.sofia.uni.fmi.mjt.spotify.log.types;

public record UserErrorLog(String user, String message) implements Log {
    @Override
    public String toString() {
        return "[" + user + "] " + message;
    }
}
