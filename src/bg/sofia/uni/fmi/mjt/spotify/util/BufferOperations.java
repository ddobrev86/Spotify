package bg.sofia.uni.fmi.mjt.spotify.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class BufferOperations {
    public static String read(ByteBuffer buffer, SocketChannel socketChannel) throws IOException {
        buffer.clear();

        int readBytes = socketChannel.read(buffer);
        if (readBytes < 0) {
            socketChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    public static void write(ByteBuffer buffer, SocketChannel socketChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes(StandardCharsets.UTF_8));
        buffer.flip();

        socketChannel.write(buffer);
    }

}
