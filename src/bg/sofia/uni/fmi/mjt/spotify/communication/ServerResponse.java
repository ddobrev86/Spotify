package bg.sofia.uni.fmi.mjt.spotify.communication;

import bg.sofia.uni.fmi.mjt.spotify.model.user.User;

public record ServerResponse(ResponseStatus status, String message, User user) {
    public static ServerResponse of(ResponseStatus status, String message, User user) {
        return new ServerResponse(status, message, user);
    }
}
