package bg.sofia.uni.fmi.mjt.spotify.command;

import java.util.List;

public class CommandCreator {
    private static List<String> getCommandArguments(String input) {
        String[] tokens = input.split(" ");

        return List.of(tokens);
    }

    public static Command createCommand(String clientInput) {
        List<String> tokens = CommandCreator.getCommandArguments(clientInput);
        String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);

        return new Command(tokens.getFirst(), args);
    }
}
