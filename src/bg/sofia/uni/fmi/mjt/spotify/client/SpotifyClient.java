package bg.sofia.uni.fmi.mjt.spotify.client;

import bg.sofia.uni.fmi.mjt.spotify.client.player.MusicPlayer;
import bg.sofia.uni.fmi.mjt.spotify.communication.ClientQuery;
import bg.sofia.uni.fmi.mjt.spotify.communication.ResponseStatus;
import bg.sofia.uni.fmi.mjt.spotify.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.spotify.model.user.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import static bg.sofia.uni.fmi.mjt.spotify.util.BufferOperations.read;
import static bg.sofia.uni.fmi.mjt.spotify.util.BufferOperations.write;

public class SpotifyClient {
    private static final Gson GSON = new Gson();
    //private static final ExecutorService MUSIC_PLAYER = Executors.newVirtualThreadPerTaskExecutor();

    private static final int DEFAULT_PORT = 4000;
    private static final int BUFFER_SIZE = 1024;
    private static final String DEFAULT_HOST = "localhost";

    private final MusicPlayer musicPlayer;

    private final int port;
    private ByteBuffer buffer;
    private User user;

    public SpotifyClient(int port) {
        this.port = port;
        this.user = null;
        this.musicPlayer = new MusicPlayer();
    }

    private String processInput(String input) {
        String email = (user == null ? null : user.email());
        return GSON.toJson(ClientQuery.of(email, input));
    }

    private ServerResponse processServerResponse(String input) {
        return GSON.fromJson(input, ServerResponse.class);
    }

    private ServerResponse handleServerCommunication(String input, SocketChannel socketChannel)
        throws IOException {
        write(buffer, socketChannel, processInput(input));
        String reply = read(buffer, socketChannel);

        return processServerResponse(reply);
    }

    private void handleOutput(ServerResponse output) {
        if (output == null) {
            return;
        } else if (output.user() != null) {
            this.user = output.user();
        } else if (output.status() == ResponseStatus.PLAY) {
            musicPlayer.play((String) output.additionalInfo());
        } else if (output.status() == ResponseStatus.STOP) {
            musicPlayer.stop();
        } else if (output.status() == ResponseStatus.DISCONNECT) {
            musicPlayer.disconnect();
        }

        System.out.println(output.message());
    }

    private void printAvailableCommands() {
        System.out.println("\nAvailable actions:");
        if (this.user == null) {
            System.out.println("register <email> <password>");
            System.out.println("login <email> <password>");
        } else {
            System.out.println("search <keywords>");
            System.out.println("top <number>");
            System.out.println("create-playlist <name_of_the_playlist>");
            System.out.println("add-song-to <name_of_the_playlist> <song>");
            System.out.println("show-playlist <name_of_the_playlist>");
            System.out.println("play <song>");
            System.out.println("stop");
        }
        System.out.println("disconnect");
        System.out.println("\nEnter command:");
    }

    public void runClient() {
        buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(DEFAULT_HOST, port));
            System.out.println("Connected to server successfully");

            String input = "";
            while (!input.equals("disconnect")) {
                printAvailableCommands();
                input = scanner.nextLine();
                ServerResponse output = handleServerCommunication(input, socketChannel);

                handleOutput(output);
            }

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }

    static void main() {
        SpotifyClient client = new SpotifyClient(DEFAULT_PORT);
        client.runClient();
    }
}
