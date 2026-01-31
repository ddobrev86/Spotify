package bg.sofia.uni.fmi.mjt.spotify.model.user;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.user.IncorrectPasswordException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.user.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.user.UserAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.user.UserNotRegisteredException;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private final Map<String, User> users;

    public UserManager() {
        this.users = new HashMap<>();
    }

    private boolean isValidPassword(String password) {
        return password.matches("^[A-Za-z0-9@#]+$");
    }

    public User registerUser(User user) {
        if (users.containsKey(user.email())) {
            throw new UserAlreadyRegisteredException("User with email " + user.email() + " is already registered");
        }

        users.put(user.email(), user);

        return user;
    }

    public User registerUser(String email, String password) {
        if (users.containsKey(email)) {
            throw new UserAlreadyRegisteredException("User with email " + email + " is already registered");
        }

        if (!isValidPassword(password)) {
            throw new InvalidPasswordException("The given password is invalid. A valid password must contain only " +
                "letters, digits, @ and #");
        }

        User userToAdd = new User(email, password);
        users.put(email, userToAdd);

        return userToAdd;
    }

    public User login(String email, String password) {
        if (!users.containsKey(email)) {
            throw new UserNotRegisteredException("User with email " + email + " is not registered");
        }

        if (!users.get(email).comparePassword(password)) {
            throw new IncorrectPasswordException("Wrong password given");
        }

        return users.get(email);
    }
}
