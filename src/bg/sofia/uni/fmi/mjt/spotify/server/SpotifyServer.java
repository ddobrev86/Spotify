package bg.sofia.uni.fmi.mjt.spotify.server;

import bg.sofia.uni.fmi.mjt.spotify.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.spotify.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.spotify.communication.ClientQuery;
import bg.sofia.uni.fmi.mjt.spotify.communication.ResponseStatus;
import bg.sofia.uni.fmi.mjt.spotify.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.InvalidArgumentCountException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.spotify.log.manager.LogManager;
import bg.sofia.uni.fmi.mjt.spotify.log.logger.PlaylistLogger;
import bg.sofia.uni.fmi.mjt.spotify.log.logger.PlaylistToSongLogger;
import bg.sofia.uni.fmi.mjt.spotify.log.logger.SystemLogger;
import bg.sofia.uni.fmi.mjt.spotify.log.logger.UserLogger;
import bg.sofia.uni.fmi.mjt.spotify.log.types.Log;
import bg.sofia.uni.fmi.mjt.spotify.log.types.ServerErrorLog;
import bg.sofia.uni.fmi.mjt.spotify.log.types.SuccessfulLog;
import bg.sofia.uni.fmi.mjt.spotify.log.types.UserErrorLog;
import bg.sofia.uni.fmi.mjt.spotify.model.song.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.model.song.PlaylistSongPair;
import bg.sofia.uni.fmi.mjt.spotify.model.song.SongManager;
import bg.sofia.uni.fmi.mjt.spotify.model.user.User;
import bg.sofia.uni.fmi.mjt.spotify.model.user.UserManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import static bg.sofia.uni.fmi.mjt.spotify.util.BufferOperations.read;
import static bg.sofia.uni.fmi.mjt.spotify.util.BufferOperations.write;

public class SpotifyServer {
    private static final Gson GSON = new Gson();

    private static final int DEFAULT_PORT = 4000;
    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";

    private static LogManager<Log> systemLogManager;
    private static LogManager<User> userLogManager;
    private static LogManager<Playlist> playlistLogManager;
    private static LogManager<PlaylistSongPair> playlistToSongManager;

    private final UserManager userManager;
    private final SongManager songManager;

    private final int port;
    private boolean isServerWorking;
    private ByteBuffer buffer;
    private Selector selector;

    private final CommandExecutor commandExecutor;
    private Map<String, String> databaseFiles;

    public SpotifyServer(int port, UserManager userManager, SongManager songManager) {
        this.port = port;
        this.userManager = userManager;
        this.songManager = songManager;
        this.commandExecutor = new CommandExecutor();

        setUpLogManagers();
        isServerWorking = true;
    }

    private void setUpLogManagers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("config.json"))) {
            databaseFiles = GSON.fromJson(reader, new TypeToken<Map<String, String>>() { }.getType());

            systemLogManager = new LogManager<>(new SystemLogger(
                new File(databaseFiles.get("logFile")),
                true)
            );
            userLogManager = new LogManager<>(new UserLogger(
                new File(databaseFiles.get("userFile")),
                true)
            );
            playlistLogManager = new LogManager<>(new PlaylistLogger(
                new File(databaseFiles.get("playlistFile")),
                true)
            );
            playlistToSongManager = new LogManager<>(new PlaylistToSongLogger(
                new File(databaseFiles.get("playlistToSongFile")),
                true));
        } catch (IOException e) {
            System.out.println("Error when connecting to database");
        }
    }

    private ClientQuery processClientInput(String input) {
        return GSON.fromJson(input, ClientQuery.class);
    }

    private String processOutput(ServerResponse serverResponse) {
        return GSON.toJson(serverResponse);
    }

    private void manageLogsInResponseProcessing(ClientQuery clientQuery, ServerResponse output) {
        String failureUser = clientQuery.email() != null ? clientQuery.email() : "non-logged-in user";
        switch (output.status()) {
            case ResponseStatus.REGISTRATION -> userLogManager.add(output.user());
            case ResponseStatus.CREATE_PLAYLIST -> playlistLogManager.add((Playlist) output.additionalInfo());
            case ResponseStatus.ADD_SONG -> playlistToSongManager.add((PlaylistSongPair) output.additionalInfo());
        }

        if (output.status() == ResponseStatus.FAILURE) {
            systemLogManager.add(new UserErrorLog(failureUser, output.logMessage()));
        } else {
            systemLogManager.add(new SuccessfulLog(output.message()));
        }
    }

    private ServerResponse processResponse(ClientQuery clientQuery) {
        ServerResponse output;
        try {
            output = commandExecutor.execute(CommandCreator.createCommand(clientQuery.command()),
                clientQuery.email(), userManager, songManager);
        } catch (InvalidArgumentCountException e) {
            output = ServerResponse.of(ResponseStatus.FAILURE,
                "Please follow the format for the command " + e.getCommand() + " and its arguments count",
                commandExecutor.logFailureMessage(clientQuery.email() + " gave invalid number of arguments for " +
                    "command " + e.getCommand(), e), null);
        } catch (InvalidCommandException e) {
            output = ServerResponse.of(ResponseStatus.FAILURE,
                "The given command is invalid",
                commandExecutor.logFailureMessage("An invalid command was given", e), null);
        }

        manageLogsInResponseProcessing(clientQuery, output);
        return output;
    }

    private void iterateKeys(Iterator<SelectionKey> keyIterator) throws IOException {
        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();

            if (key.isReadable()) {
                SocketChannel clientChannel = (SocketChannel) key.channel();
                String clientInput = read(buffer, clientChannel);
                if (clientInput == null) {
                    continue;
                } else if (clientInput.equals("disconnect")) {
                    clientChannel.close();
                }

                ClientQuery clientQuery = processClientInput(clientInput);

                ServerResponse output = processResponse(clientQuery);

                write(buffer, clientChannel, processOutput(output));

            } else if (key.isAcceptable()) {
                accept(selector, key);
            }

            keyIterator.remove();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, this.port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            isServerWorking = true;

            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    iterateKeys(keyIterator);

                } catch (Exception e) {
                    System.out.println("Error occurred while processing client request: " + e.getMessage());
                    systemLogManager.add(new ServerErrorLog("Error occurred while processing client request: "
                        + e.getMessage()));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("failed to start server", e);
        }
    }

    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }

        userLogManager.shutdown();
        playlistLogManager.shutdown();
        playlistToSongManager.shutdown();
        systemLogManager.shutdown();
    }

    private void loadFromDataBase(String fileName, String errorMessage, Consumer<String> loader) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    loader.accept(line);
                } catch (IllegalArgumentException e) {
                    systemLogManager.add(new ServerErrorLog("Could not load record " + line));
                }
            }

        } catch (IOException e) {
            System.out.println(errorMessage);
            systemLogManager.add(new ServerErrorLog(errorMessage));
        }
    }

    public void setUpData() {
        loadFromDataBase(databaseFiles.get("userFile"),
            "Could not load the users from the database",
            userManager::registerUser);
        loadFromDataBase(databaseFiles.get("playlistFile"),
            "Could not load the playlists from the database",
            songManager::createPlaylist);
        loadFromDataBase(databaseFiles.get("songFile"),
            "Could not load the songs from the database",
            songManager::createSong);
        loadFromDataBase(databaseFiles.get("playlistToSongFile"),
            "Could not load the songs into playlists from the database",
            songManager::matchPlaylistToSong);
    }

    static void main() {
        SpotifyServer server = new SpotifyServer(DEFAULT_PORT, new UserManager(), new SongManager());
        server.setUpData();

        server.start();
    }
}
