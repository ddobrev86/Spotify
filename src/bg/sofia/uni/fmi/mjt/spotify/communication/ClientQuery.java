package bg.sofia.uni.fmi.mjt.spotify.communication;

public record ClientQuery(String email, String command) {

    public static ClientQuery of(String email, String command) {
        return new ClientQuery(email, command);
    }
}
