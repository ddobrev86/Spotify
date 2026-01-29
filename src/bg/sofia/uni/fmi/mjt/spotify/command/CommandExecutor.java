package bg.sofia.uni.fmi.mjt.spotify.command;

import bg.sofia.uni.fmi.mjt.spotify.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.spotify.model.song.SongManager;
import bg.sofia.uni.fmi.mjt.spotify.model.user.User;
import bg.sofia.uni.fmi.mjt.spotify.model.user.UserManager;

public class CommandExecutor {
    private static final String SEARCH = "search";
    private static final String TOP = "top";
    private static final String CREATE_PLAYLIST = "create-playlist";
    private static final String ADD_SONG_TO = "add-song-to";
    private static final String SHOW_PLAYLIST = "show-playlist";
    private static final String PLAY = "play";
    private static final String STOP = "stop";

    private static final String REGISTER = "register";
    private static final String LOGIN = "login";
    private static final String DISCONNECT = "disconnect";

    private static final int ACCOUNT_OPERATIONS_ARGUMENT_COUNT = 2;

    private final UserManager userManager;
    private final SongManager songManager;

    public CommandExecutor(UserManager userManager, SongManager songManager) {
        this.userManager = userManager;
        this.songManager = songManager;
    }

    public ServerResponse execute(Command cmd) {
        return switch (cmd.command()) {
            case REGISTER -> register(cmd.arguments());
            case LOGIN -> login(cmd.arguments());
            //case DISCONNECT -> disconnect();
            default -> invalidCommand();
        };
    }

    private ServerResponse register(String[] args) {
        if (args.length != ACCOUNT_OPERATIONS_ARGUMENT_COUNT) {
            throw new IllegalArgumentException(REGISTER + " accepts only two values: email and password." +
                "Example: register <email> <password>");
        }

        String email = args[0];
        String password = args[1];

        User registeredUser = userManager.registerUser(email, password);
        return ServerResponse.of("User with email " + email + " registered in successfully", registeredUser);
    }

    private ServerResponse login(String[] args) {
        if (args.length != ACCOUNT_OPERATIONS_ARGUMENT_COUNT) {
            throw new IllegalArgumentException(LOGIN + " accepts only two values: email and password." +
                "Example: login <email> <password>");
        }

        String email = args[0];
        String password = args[1];

        User loggedInUser = userManager.login(email, password);
        return ServerResponse.of("User " + email + " logged in successfully", loggedInUser);
    }

//    private String disconnect() {
//        return "";
//    }

    private ServerResponse invalidCommand() {
        return ServerResponse.of("", null);
    }
}
