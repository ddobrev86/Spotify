package bg.sofia.uni.fmi.mjt.spotify.model.user;

public class User {
    private String email;
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public boolean isCorrectPassword(String password) {
        return this.password.equals(password);
    }

    public String email() {
        return this.email;
    }
}
