package bg.sofia.uni.fmi.mjt.spotify.log.types;

public record SuccessfulLog(String message) implements Log {
    @Override
    public String toString() {
        return "[Success] " + message;
    }
}
