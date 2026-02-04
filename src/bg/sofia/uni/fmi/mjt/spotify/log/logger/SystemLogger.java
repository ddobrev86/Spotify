package bg.sofia.uni.fmi.mjt.spotify.log.logger;

import bg.sofia.uni.fmi.mjt.spotify.log.types.Log;

import java.io.File;
import java.io.IOException;

public class SystemLogger extends Logger<Log> {
    public SystemLogger(File file, boolean append) throws IOException {
        super(file, append);
    }

    @Override
    public void log(Log toBeLogged) throws IOException {
        this.writer.write(toBeLogged.toString() + System.lineSeparator());
        this.writer.flush();
    }
}
