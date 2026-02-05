package bg.sofia.uni.fmi.mjt.spotify.exceptions;

public class InvalidArgumentCountException extends RuntimeException {
    private final String command;

    public InvalidArgumentCountException(String message, String command) {
        this.command = command;
        super(message);
    }

    public String getCommand() {
        return command;
    }
}
