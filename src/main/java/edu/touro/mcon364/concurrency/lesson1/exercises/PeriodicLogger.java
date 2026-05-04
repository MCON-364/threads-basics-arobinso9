package edu.touro.mcon364.concurrency.lesson1.exercises;

/**
 * Exercise 3: sleep(), isAlive(), and the daemon flag
 *
 * A PeriodicLogger should log a message on a background thread at a fixed
 * interval for a fixed number of ticks, then stop.
 *
 * Requirements:
 * - start(): create a background thread that sleeps for intervalMs milliseconds,
 *   then appends one line to the log, and repeats this exactly 'ticks' times total.
 *   The thread must be marked as a DAEMON thread before it is started.
 *   The thread must be given the name "periodic-logger".
 *   start() must return BEFORE the background thread has finished (non-blocking).
 *
 * - isRunning(): return true if the background thread exists and is still alive,
 *   false otherwise.
 *
 * - awaitCompletion(): block the calling thread until the background thread finishes.
 *
 * - getLog(): return the messages appended so far (one per tick).
 *
 * Hint: Thread.sleep(intervalMs) pauses the current thread.
 *       thread.isAlive() tells you whether a thread is still running.
 *       setDaemon(true) must be called BEFORE start().
 */
public class PeriodicLogger {

    private final int ticks;
    private final long intervalMs;
    private final java.util.List<String> log = new java.util.ArrayList<>();
    private Thread worker;

    public PeriodicLogger(int ticks, long intervalMs) {
        if (ticks <= 0) throw new IllegalArgumentException("ticks must be positive");
        if (intervalMs < 0) throw new IllegalArgumentException("intervalMs must be non-negative");
        this.ticks = ticks;
        this.intervalMs = intervalMs;
    }

    /**
     * Create, configure, and start the background thread.
     * Must return before the thread finishes (i.e. do NOT join here).
     */
    public void start() {
        // TODO: create a daemon thread named "periodic-logger" that
        //       sleeps for intervalMs then appends "tick N" (1-based) to log,
        //       repeating 'ticks' times total, then starts it.

        worker = new Thread(() -> { // we instantiate the worker thread first
            try {
                for (int i = 1; i <= ticks; i++) {
                    // we call sleep on the Thread keyword bc Sleep is a static method. Its stelling the CPU its gng to sleep
                    // the difference is btwn putting urself to sleep and one thread putting another thread to sleep
                    // t.sleep(1000) --> telling Thread T, go to sleep for 1 second.
                    // Thread.sleep(1000) --> I (the current thread) am going to sleep.
                    Thread.sleep(intervalMs); // pause for the interval.
                    log.add("tick " + i);     // 1-based log entry
                }
            } catch (InterruptedException e) {
                // if the thread is interrupted while sleeping, we stop
                Thread.currentThread().interrupt();
            }
        }, "periodic-logger");


        worker.setDaemon(true); // must be set BEFORE start() - we need to say what ttpe of thread it ia
        worker.start();
    }

    /**
     * Returns true while the background thread is still running.
     */
    public boolean isRunning() {
        // TODO: return whether the worker thread is alive
        if(worker.isAlive())
            return true;
        return false;
    }

    /**
     * Blocks until the background thread finishes.
     */
    public void awaitCompletion() throws InterruptedException {
        // TODO: join the worker thread
        worker.join();
    }

    /** Returns the log messages collected so far. */
    public java.util.List<String> getLog() {
        return java.util.Collections.unmodifiableList(log);
    }
}

