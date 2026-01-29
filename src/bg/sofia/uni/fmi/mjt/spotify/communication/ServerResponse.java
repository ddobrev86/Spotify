package bg.sofia.uni.fmi.mjt.spotify.communication;

import bg.sofia.uni.fmi.mjt.spotify.model.user.User;

public class ServerResponse {
    private final String message;
    private final User user;

    private ServerResponse(String message, User user) {
        this.message = message;
        this.user = user;
    }

    public static ServerResponse of(String message, User user) {
        return new ServerResponse(message, user);
    }
}
