package bg.sofia.uni.fmi.mjt.spotify.log.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Logger<T> {
    protected final BufferedWriter writer;

    public Logger(File file) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(file, true));
    }

    public abstract void log(T toBeLogged) throws IOException;
}
