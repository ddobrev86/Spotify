package bg.sofia.uni.fmi.mjt.spotify.command;

import bg.sofia.uni.fmi.mjt.spotify.communication.ResponseStatus;
import bg.sofia.uni.fmi.mjt.spotify.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.spotify.model.song.SongManager;
import bg.sofia.uni.fmi.mjt.spotify.model.user.User;
import bg.sofia.uni.fmi.mjt.spotify.model.user.UserManager;

import java.util.List;

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
    private static final int SEARCH_MIN_ARGUMENT_COUNT = 1;
    private static final int TOP_ARGUMENT_COUNT = 1;
    private static final int CREATE_PLAYLIST_ARGUMENT_COUNT = 1;
    private static final int ADD_SONG_TO_PLAYLIST_ARGUMENT_COUNT = 2;
    private static final int SHOW_PLAYLIST_ARGUMENT_COUNT = 1;
    private static final int PLAY_ARGUMENT_COUNT = 1;

    public CommandExecutor() {
    }

    public ServerResponse execute(Command cmd,
                                  String userEmail,
                                  UserManager userManager,
                                  SongManager songManager) {
        return switch (cmd.command()) {
            case REGISTER -> register(cmd.arguments(), userManager);
            case LOGIN -> login(cmd.arguments(), userManager);
            case SEARCH -> search(cmd.arguments(), songManager);
            case TOP -> top(cmd.arguments(), songManager);
            case CREATE_PLAYLIST -> createPlaylist(cmd.arguments(), userEmail, songManager);
            case ADD_SONG_TO -> addSongToPlaylist(cmd.arguments(), userEmail, songManager);
            case SHOW_PLAYLIST -> showPlaylist(cmd.arguments(), songManager);
            case PLAY -> play(cmd.arguments(), songManager);
            //case DISCONNECT -> disconnect();
            default -> throw new InvalidCommandException("The given command is invalid");
        };
    }

    private ServerResponse register(String[] args, UserManager userManager) {
        if (args.length != ACCOUNT_OPERATIONS_ARGUMENT_COUNT) {
            throw new IllegalArgumentException(REGISTER + " accepts only two values: email and password." +
                "Example: " + REGISTER + " <email> <password>");
        }

        String email = args[0];
        String password = args[1];

        User registeredUser = userManager.registerUser(email, password);
        return ServerResponse.of(ResponseStatus.SUCCESS,
            "User with email " + email + " registered in successfully", registeredUser);
    }

    private ServerResponse login(String[] args, UserManager userManager) {
        if (args.length != ACCOUNT_OPERATIONS_ARGUMENT_COUNT) {
            throw new IllegalArgumentException(LOGIN + " accepts only two values: email and password." +
                "Example: " + LOGIN + " <email> <password>");
        }

        String email = args[0];
        String password = args[1];

        User loggedInUser = userManager.login(email, password);
        return ServerResponse.of(ResponseStatus.SUCCESS, "User " + email + " logged in successfully", loggedInUser);
    }

    private String showSongsAsString(List<String> songs, String emptyMessage) {
        if (songs.isEmpty()) {
            return emptyMessage;
        }

        return String.join(System.lineSeparator(), songs);
    }

    private ServerResponse search(String[] args, SongManager songManager) {
        if (args.length < SEARCH_MIN_ARGUMENT_COUNT) {
            throw new IllegalArgumentException(SEARCH + " must be given at least one keyword." +
                "Example: " + SEARCH + " <keyword> [<keywords>]");
        }

        List<String> result = songManager.search(args);

        return ServerResponse.of(ResponseStatus.SUCCESS, showSongsAsString(result,
            "There are no songs that match the given keywords"), null);
    }

    private ServerResponse top(String[] args, SongManager songManager) {
        if (args.length != TOP_ARGUMENT_COUNT) {
            throw new IllegalArgumentException(TOP + " accepts only one value: number." +
                "Example: " + TOP + " <number>");
        }

        int number = Integer.parseInt(args[0]);
        List<String> result = songManager.top(number);

        return ServerResponse.of(ResponseStatus.SUCCESS, showSongsAsString(result,
            "There are no songs to show"), null);
    }

    private ServerResponse createPlaylist(String[] args, String userEmail, SongManager songManager) {
        if (args.length != CREATE_PLAYLIST_ARGUMENT_COUNT) {
            throw new IllegalArgumentException(CREATE_PLAYLIST + " accepts only one value: name_of_playlist." +
                "Example: " + CREATE_PLAYLIST + " <name_of_playlist>");
        }

        String playlistName = args[0];
        songManager.createPlaylist(playlistName, userEmail);

        return ServerResponse.of(ResponseStatus.SUCCESS,
            "Playlist " + playlistName + " created successfully", null);
    }

    private ServerResponse addSongToPlaylist(String[] args, String userEmail, SongManager songManager) {
        if (args.length != ADD_SONG_TO_PLAYLIST_ARGUMENT_COUNT) {
            throw new IllegalArgumentException(ADD_SONG_TO + " accepts only two values: name_of_playlist and song." +
                "Example: " + ADD_SONG_TO + " <name_of_playlist> <song>");
        }

        String playlistName = args[0];
        String songName = args[1];
        songManager.addSongToPlaylist(playlistName, songName, userEmail);

        return ServerResponse.of(ResponseStatus.SUCCESS,
            "Song " + songName + " added successfully to " + playlistName, null);
    }

    private ServerResponse showPlaylist(String[] args, SongManager songManager) {
        if (args.length != SHOW_PLAYLIST_ARGUMENT_COUNT) {
            throw new IllegalArgumentException(SHOW_PLAYLIST + " accepts only one value: name_of_playlist." +
                "Example: " + SHOW_PLAYLIST + " <name_of_playlist>");
        }

        String playlistName = args[0];
        List<String> result = songManager.showPlaylist(playlistName);

        return ServerResponse.of(ResponseStatus.SUCCESS, showSongsAsString(result,
            "Playlist is empty"), null);
    }

    private ServerResponse play(String[] args, SongManager songManager) {
        if (args.length != PLAY_ARGUMENT_COUNT) {
            throw new IllegalArgumentException(PLAY + " accepts only one value: song." +
                "Example: " + PLAY + " <song>");
        }

        String songName = args[0];
        songManager.playSong(songName);

        return ServerResponse.of(ResponseStatus.SUCCESS, "Playing...", null);
    }
}
