package bg.sofia.uni.fmi.mjt.spotify.exceptions;

public class InvalidArgumentCountException extends RuntimeException {
    private String command;

    public InvalidArgumentCountException(String message) {
        super(message);
    }

    public InvalidArgumentCountException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidArgumentCountException(String message, String command) {
        this.command = command;
        super(message);
    }

    public InvalidArgumentCountException(String message, Throwable cause, String command) {
        this.command = command;
        super(message, cause);
    }

    public String getCommand() {
        return command;
    }
}
