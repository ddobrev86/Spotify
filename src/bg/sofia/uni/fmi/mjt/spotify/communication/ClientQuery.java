package bg.sofia.uni.fmi.mjt.spotify.communication;

public class ClientQuery {
    private final String email;
    private final String command;

    private ClientQuery(String email, String command) {
        this.email = email;
        this.command = command;
    }

    public static ClientQuery of(String email, String command) {
        return new ClientQuery(email, command);
    }

    public String email() {
        return email;
    }

    public String command() {
        return command;
    }
}
