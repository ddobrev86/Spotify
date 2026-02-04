package bg.sofia.uni.fmi.mjt.spotify.log.logger;

import bg.sofia.uni.fmi.mjt.spotify.model.user.User;

import java.io.File;
import java.io.IOException;

public class UserLogger extends Logger<User> {

    public UserLogger(File file, boolean append) throws IOException {
        super(file, append);
    }

    @Override
    public void log(User toBeLogged)  throws IOException {
        this.writer.write(toBeLogged.email() + "," + toBeLogged.password() + System.lineSeparator());
        this.writer.flush();
    }
}
