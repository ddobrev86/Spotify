package bg.sofia.uni.fmi.mjt.spotify.communication;

import bg.sofia.uni.fmi.mjt.spotify.model.user.User;

public record ServerResponse(ResponseStatus status, String message, String logMessage, User user,
                             Object additionalInfo) {
    public static ServerResponse of(ResponseStatus status,
                                    String message,
                                    String logMessage,
                                    User user) {
        return new ServerResponse(status, message, logMessage, user, null);
    }

    public static ServerResponse of(ResponseStatus status,
                                    String message,
                                    String logMessage,
                                    User user,
                                    Object additionalInfo) {
        return new ServerResponse(status, message, logMessage, user, additionalInfo);
    }
}
