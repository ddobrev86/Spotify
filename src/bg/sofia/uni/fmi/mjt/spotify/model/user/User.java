package bg.sofia.uni.fmi.mjt.spotify.model.user;

public record User(String email, String password) {
    private static final int ARGUMENT_COUNT = 2;

    private static final int EMAIL_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;

    public boolean comparePassword(String password) {
        return this.password.equals(password);
    }

    public static User of(String line) {
        String[] tokens = line.split(",");
        if (tokens.length != ARGUMENT_COUNT) {
            throw new IllegalArgumentException("Invalid input for user");
        }

        String email = tokens[EMAIL_INDEX];
        String password = tokens[PASSWORD_INDEX];

        return new User(email, password);
    }
}
