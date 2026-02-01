package bg.sofia.uni.fmi.mjt.spotify.log.logger;

import bg.sofia.uni.fmi.mjt.spotify.log.types.Log;

import java.io.IOException;

public class SystemLogger extends Logger<Log> {
    public SystemLogger(String fileName) throws IOException {
        super(fileName);
    }

    @Override
    public void log(Log toBeLogged) throws IOException {
        this.writer.write(toBeLogged.toString());
    }
}
