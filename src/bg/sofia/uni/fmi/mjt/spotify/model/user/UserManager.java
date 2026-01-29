package bg.sofia.uni.fmi.mjt.spotify.model.user;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.user.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.user.UserAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.user.UserNotRegisteredException;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, User> users;

    public UserManager() {
        this.users = new HashMap<>();
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

        User userToAdd = new User(email, password);
        users.put(email, userToAdd);

        return userToAdd;
    }

    public User login(String email, String password) {
        if (!users.containsKey(email)) {
            throw new UserNotRegisteredException("User with email " + email + " is not registered");
        }

        if (!users.get(email).isCorrectPassword(password)) {
            throw new InvalidPasswordException("Wrong password given");
        }

        return users.get(email);
    }
}
