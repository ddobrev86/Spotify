package bg.sofia.uni.fmi.mjt.spotify.log.types;

public record ServerErrorLog(String message) implements Log {

    @Override
    public String toString() {
        return "[Server Error] " + ": " + message;
    }
}
