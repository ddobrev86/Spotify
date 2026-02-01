package bg.sofia.uni.fmi.mjt.spotify.log.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Logger<T> {
    protected final String fileName;
    protected final BufferedWriter writer;

    public Logger(String fileName) throws IOException {
        this.fileName = fileName;
        this.writer = new BufferedWriter(new FileWriter(fileName, true));
    }

    public abstract void log(T toBeLogged) throws IOException;
}
