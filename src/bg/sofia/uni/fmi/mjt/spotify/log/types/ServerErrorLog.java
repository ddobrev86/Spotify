package bg.sofia.uni.fmi.mjt.spotify.log.types;

public record ServerErrorLog(String errorType, String message) implements Log {

    @Override
    public String toString() {
        return "[Server Error] " + errorType + ": " + message;
    }
}
