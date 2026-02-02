package bg.sofia.uni.fmi.mjt.spotify.command;

import bg.sofia.uni.fmi.mjt.spotify.communication.ResponseStatus;
import bg.sofia.uni.fmi.mjt.spotify.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.InvalidArgumentCountException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.song.InvalidPlaylistOwnershipException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.song.PlaylistAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.song.PlaylistNotFoundException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.song.SongAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.song.SongNotFoundException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.user.IncorrectPasswordException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.user.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.user.UserAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.user.UserNotRegisteredException;
import bg.sofia.uni.fmi.mjt.spotify.model.song.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.model.song.PlaylistSongPair;
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
        if (cmd.command().equals(DISCONNECT)) {
            return disconnect(userEmail);
        }

        if (userEmail == null) {
            return switch (cmd.command()) {
                case REGISTER -> register(cmd.arguments(), userManager);
                case LOGIN -> login(cmd.arguments(), userManager);
                default -> throw new InvalidCommandException("The given command is invalid");
            };
        } else {
            return switch (cmd.command()) {
                case SEARCH -> search(cmd.arguments(), songManager, userEmail);
                case TOP -> top(cmd.arguments(), songManager, userEmail);
                case CREATE_PLAYLIST -> createPlaylist(cmd.arguments(), songManager, userEmail);
                case ADD_SONG_TO -> addSongToPlaylist(cmd.arguments(), songManager, userEmail);
                case SHOW_PLAYLIST -> showPlaylist(cmd.arguments(), songManager, userEmail);
                case PLAY -> play(cmd.arguments(), songManager, userEmail);
                case STOP -> stop(userEmail);
                default -> throw new InvalidCommandException("The given command is invalid");
            };
        }
    }

    private ServerResponse register(String[] args, UserManager userManager) {
        if (args.length != ACCOUNT_OPERATIONS_ARGUMENT_COUNT) {
            throw new InvalidArgumentCountException(REGISTER + " accepts only two values: email and password." +
                "Example: " + REGISTER + " <email> <password>", REGISTER);
        }

        String email = args[0];
        String password = args[1];

        try {
            User registeredUser = userManager.registerUser(email, password);
            return ServerResponse.of(ResponseStatus.REGISTRATION,
                "Registration completed successfully",
                email + " registered in the system", registeredUser);
        } catch (UserAlreadyRegisteredException e) {
            return ServerResponse.of(ResponseStatus.FAILURE,
                "User with email " + email + " is already registered",
                "Attempt to re-register with an existing email " + email, null);
        } catch (InvalidPasswordException e) {
            return ServerResponse.of(ResponseStatus.FAILURE,
                "The given password is invalid. A valid password must contain only  letters, digits, @ and #",
                "Invalid password given on registration", null);
        }
    }

    private ServerResponse login(String[] args, UserManager userManager) {
        if (args.length != ACCOUNT_OPERATIONS_ARGUMENT_COUNT) {
            throw new InvalidArgumentCountException(LOGIN + " accepts only two values: email and password." +
                "Example: " + LOGIN + " <email> <password>", LOGIN);
        }

        String email = args[0];
        String password = args[1];

        try {
            User loggedInUser = userManager.login(email, password);
            return ServerResponse.of(ResponseStatus.DEFAULT_SUCCESS, "User " + email + " logged in successfully",
                "", loggedInUser);
        } catch (UserNotRegisteredException e) {
            return ServerResponse.of(ResponseStatus.FAILURE,
                "You are not registered into the system",
                "User " + email + " attempted to login without a registration", null);
        } catch (IncorrectPasswordException e) {
            return ServerResponse.of(ResponseStatus.FAILURE,
                "Wrong password!",
                "User " + email + " gave a wrong password upon login", null);
        }
    }

    private String showSongsAsString(List<String> songs, String emptyMessage) {
        if (songs.isEmpty()) {
            return emptyMessage;
        }

        return String.join(System.lineSeparator(), songs);
    }

    private ServerResponse search(String[] args, SongManager songManager, String userEmail) {
        if (args.length < SEARCH_MIN_ARGUMENT_COUNT) {
            throw new InvalidArgumentCountException(SEARCH + " must be given at least one keyword." +
                "Example: " + SEARCH + " <keyword> [<keywords>]", SEARCH);
        }

        List<String> result = songManager.search(args);

        return ServerResponse.of(ResponseStatus.DEFAULT_SUCCESS, showSongsAsString(result,
            "There are no songs that match the given keywords"),
            "", null);
    }

    private ServerResponse top(String[] args, SongManager songManager, String userEmail) {
        if (args.length != TOP_ARGUMENT_COUNT) {
            throw new InvalidArgumentCountException(TOP + " accepts only one value: number." +
                "Example: " + TOP + " <number>", TOP);
        }

        try {
            int number = Integer.parseUnsignedInt(args[0]);
            List<String> result = songManager.top(number);

            return ServerResponse.of(ResponseStatus.DEFAULT_SUCCESS, showSongsAsString(result,
                    "There are no songs to show"),
                userEmail + " searched top " + number + " most listened songs", null);
        } catch (NumberFormatException e) {
            return ServerResponse.of(ResponseStatus.FAILURE,
                "Please enter a valid positive number",
                "Unparsable number given to 'top' command", null);
        }
    }

    private ServerResponse createPlaylist(String[] args, SongManager songManager, String userEmail) {
        if (args.length != CREATE_PLAYLIST_ARGUMENT_COUNT) {
            throw new InvalidArgumentCountException(CREATE_PLAYLIST + " accepts only one value: name_of_playlist." +
                "Example: " + CREATE_PLAYLIST + " <name_of_playlist>", CREATE_PLAYLIST);
        }

        String playlistName = args[0];
        try {
            Playlist playlistToAdd = songManager.createPlaylist(playlistName, userEmail);

            return ServerResponse.of(ResponseStatus.CREATE_PLAYLIST,
                "Playlist " + playlistName + " created successfully",
                userEmail + " created playlist " + playlistName, null, playlistToAdd);
        } catch (PlaylistAlreadyExistsException e) {
            return ServerResponse.of(ResponseStatus.FAILURE,
                "Playlist " + playlistName + " already exists",
                userEmail + " tried to create playlist " + playlistName + ", which already exists", null);
        }
    }

    private ServerResponse addSongToPlaylist(String[] args, SongManager songManager, String userEmail) {
        if (args.length != ADD_SONG_TO_PLAYLIST_ARGUMENT_COUNT) {
            throw new InvalidArgumentCountException(ADD_SONG_TO +
                " accepts only two values: name_of_playlist and song." +
                "Example: " + ADD_SONG_TO + " <name_of_playlist> <song>", ADD_SONG_TO);
        }

        String playlistName = args[0];
        String songName = args[1];

        try {
            songManager.addSongToPlaylist(playlistName, songName, userEmail);
            return ServerResponse.of(ResponseStatus.ADD_SONG,
                "Song " + songName + " added successfully to " + playlistName,
                userEmail + " added song " + songName + " to playlist " + playlistName, null,
                new PlaylistSongPair(playlistName, songName));
        } catch (PlaylistNotFoundException e) {
            return ServerResponse.of(ResponseStatus.FAILURE, "There isn't a playlist with this name",
                userEmail + " tried to add a song to non-existing playlist", null);
        } catch (SongNotFoundException e) {
            return ServerResponse.of(ResponseStatus.FAILURE, "There isn't a song with this name",
                userEmail + " tried to add a non-existing song to a playlist", null);
        } catch (InvalidPlaylistOwnershipException e) {
            return ServerResponse.of(ResponseStatus.FAILURE, "This playlist is private. You can't add a song to it",
                userEmail + " tried to add a song to a private playlist, that he does not own", null);
        } catch (SongAlreadyExistsException e) {
            return ServerResponse.of(ResponseStatus.FAILURE, "The song is already in the playlist",
                userEmail + " tried to re-add " + songName + " to playlist " + playlistName, null);
        }
    }

    private ServerResponse showPlaylist(String[] args, SongManager songManager, String userEmail) {
        if (args.length != SHOW_PLAYLIST_ARGUMENT_COUNT) {
            throw new InvalidArgumentCountException(SHOW_PLAYLIST + " accepts only one value: name_of_playlist." +
                "Example: " + SHOW_PLAYLIST + " <name_of_playlist>", SHOW_PLAYLIST);
        }

        String playlistName = args[0];
        try {
            List<String> result = songManager.showPlaylist(playlistName);

            return ServerResponse.of(ResponseStatus.DEFAULT_SUCCESS, showSongsAsString(result,
                "Playlist is empty"),
                "show_playlist command executed by " + userEmail + " for playlist " + playlistName, null);
        } catch (PlaylistNotFoundException e) {
            return ServerResponse.of(ResponseStatus.FAILURE,
                "There isn't a playlist with this name",
                userEmail + " tried to view a non-existing playlist", null);
        }
    }

    private ServerResponse play(String[] args, SongManager songManager, String userEmail) {
        if (args.length != PLAY_ARGUMENT_COUNT) {
            throw new InvalidArgumentCountException(PLAY + " accepts only one value: song." +
                "Example: " + PLAY + " <song>", PLAY);
        }

        String songName = args[0];

        try {
            String songUrl = songManager.playSong(songName).url();

            return ServerResponse.of(ResponseStatus.PLAY, "Playing " + songName + "...",
                userEmail + " started playing song " + songName, null, songUrl);
        } catch (SongNotFoundException e) {
            return ServerResponse.of(ResponseStatus.FAILURE,
                "There isn't a song with this name",
                userEmail + " tried to initiatePlay a non-existing song", null);
        }
    }

    private ServerResponse stop(String userEmail) {
        return ServerResponse.of(ResponseStatus.STOP,
            "You have stopped the song",
            userEmail + " has stopped current listening",
            null);
    }

    private ServerResponse disconnect(String userEmail) {
        String user = userEmail != null ? userEmail : "non-logged-in user";

        return ServerResponse.of(ResponseStatus.DISCONNECT,
            "You have disconnected from the system",
            user + " has disconnected from the system",
            null);
    }
}
