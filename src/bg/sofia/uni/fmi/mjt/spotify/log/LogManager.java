package bg.sofia.uni.fmi.mjt.spotify.log;

import bg.sofia.uni.fmi.mjt.spotify.log.logger.Logger;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LogManager<T> {
    private static final int THREAD_COUNT = 1;
    private static final int INITIAL_DELAY = 10;
    private static final int DELAY = 20;

    private final Queue<T> logsToProcess;
    private final ScheduledExecutorService scheduler  = Executors.newScheduledThreadPool(THREAD_COUNT);
    private final Logger<T> logger;

    public LogManager(Logger<T> logger) {
        this.logsToProcess = new PriorityQueue<>();
        this.logger = logger;

        scheduler.scheduleWithFixedDelay(this::write, INITIAL_DELAY, DELAY, TimeUnit.SECONDS);
    }

    public void add(T log) {
        logsToProcess.add(log);
    }

    private void write() {
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
