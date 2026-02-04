package bg.sofia.uni.fmi.mjt.spotify.log.manager;

import bg.sofia.uni.fmi.mjt.spotify.log.logger.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class LogManager<T> extends DatabaseWriter<T> {
    private final Queue<T> logsToProcess;

    public LogManager(Logger<T> logger) {
        this.logsToProcess = new LinkedList<>();

        super(logger);
    }

    public LogManager(Logger<T> logger, int delay) {
        this.logsToProcess = new LinkedList<>();

        super(logger, delay);
    }

    public void add(T log) {
        logsToProcess.add(log);
    }

    void write() {
        while (!logsToProcess.isEmpty()) {
            T toBeLogged = logsToProcess.peek();
            try {
                logger.log(toBeLogged);
                logsToProcess.poll();
            } catch (IOException e) {
                break;
            }
        }
    }
}
