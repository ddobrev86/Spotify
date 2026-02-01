package bg.sofia.uni.fmi.mjt.spotify.model.user;

public class User {
    private static final int EMAIL_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;

    private String email;
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public boolean comparePassword(String password) {
        return this.password.equals(password);
    }

    public String email() {
        return this.email;
    }

    public String password() {
        return this.password;
    }

    public static User of(String line) {
        String[] tokens = line.split(",");

        String email = tokens[EMAIL_INDEX];
        String password = tokens[PASSWORD_INDEX];

        return new User(email, password);
    }
}
