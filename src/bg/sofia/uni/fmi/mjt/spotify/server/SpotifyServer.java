package bg.sofia.uni.fmi.mjt.spotify.server;

import bg.sofia.uni.fmi.mjt.spotify.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.spotify.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.spotify.communication.ClientQuery;
import bg.sofia.uni.fmi.mjt.spotify.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.spotify.model.song.SongManager;
import bg.sofia.uni.fmi.mjt.spotify.model.user.UserManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static bg.sofia.uni.fmi.mjt.spotify.util.BufferOperations.read;
import static bg.sofia.uni.fmi.mjt.spotify.util.BufferOperations.write;

public class SpotifyServer {
    private static final Gson GSON = new Gson();

    private static final int DEFAULT_PORT = 4000;
    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";

    private final UserManager userManager;
    private final SongManager songManager;

    private final int port;

    private boolean isServerWorking;

    private ByteBuffer buffer;
    private Selector selector;

    private final CommandExecutor commandExecutor;

    public SpotifyServer(int port, UserManager userManager, SongManager songManager) {
        this.port = port;
        this.userManager = userManager;
        this.songManager = songManager;
        this.commandExecutor = new CommandExecutor();
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

                } catch (IOException e) {
                    System.out.println("Error occurred while processing client request: " + e.getMessage());
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
    }

    private ClientQuery processClientInput(String input) {
        return GSON.fromJson(input, ClientQuery.class);
    }

    private String processOutput(ServerResponse serverResponse) {
        return GSON.toJson(serverResponse);
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

                ServerResponse output = commandExecutor.execute(CommandCreator.createCommand(clientQuery.command()),
                    clientQuery.email(),
                    userManager,
                    songManager);

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

    static void main() {
        SpotifyServer server = new SpotifyServer(DEFAULT_PORT, new UserManager(), new SongManager());
        server.start();
    }
}
