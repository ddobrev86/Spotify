package bg.sofia.uni.fmi.mjt.spotify.log.manager;

import bg.sofia.uni.fmi.mjt.spotify.log.logger.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class DatabaseWriter<T> {
    private static final int THREAD_COUNT = 1;
    private static final int INITIAL_DELAY = 10;
    private static final int DEFAULT_DELAY = 20;

    protected final ScheduledExecutorService scheduler  = Executors.newScheduledThreadPool(THREAD_COUNT);
    protected final Logger<T> logger;

    public DatabaseWriter(Logger<T> logger) {
        this.logger = logger;
        scheduler.scheduleWithFixedDelay(this::write, INITIAL_DELAY, DEFAULT_DELAY, TimeUnit.SECONDS);
    }

    public DatabaseWriter(Logger<T> logger, int delay) {
        this.logger = logger;
        scheduler.scheduleWithFixedDelay(this::write, INITIAL_DELAY, delay, TimeUnit.SECONDS);
    }

    abstract void write();

    public void shutdown() {
        this.scheduler.shutdown();
    }
}
