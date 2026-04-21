package edu.touro.mcon364.concurrency.lesson2.exercises;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Exercise 4 — Submit tasks to a fixed thread pool.
 *
 * Scenario: instead of creating one {@code Thread} per task, we hand all work
 * to an {@link ExecutorService} and let it manage a fixed pool of workers.
 *
 * Your tasks:
 *
 * (A) Implement {@link #processBatch(List)}.
 *     - Create a fixed thread pool with {@link #POOL_SIZE} threads.
 *     - Submit each task name as a {@code Runnable} that:
 *         1. Increments {@code completedCount}.
 *         2. Adds the thread name to {@code workerNames} (use {@code synchronized}).
 *     - After all submissions, shut down the pool and wait for termination
 *       (up to 10 seconds).
 *
 * (B) Notice that the number of distinct thread names you see is at most
 *     {@link #POOL_SIZE}, proving that threads are reused.
 */
public class BatchTaskExecutor {

    public static final int POOL_SIZE = 3;

    private final AtomicInteger completedCount = new AtomicInteger(0);
    private final List<String>  workerNames    = new ArrayList<>();

    /**
     * Process each name in {@code taskNames} using a fixed thread pool of
     * {@link #POOL_SIZE} threads.
     *
     * @param taskNames list of task identifiers to process
     */
    public void processBatch(List<String> taskNames) throws InterruptedException {
        // TODO: create a thread pool whose size is bounded by POOL_SIZE

        for (String name : taskNames) {
            // TODO: hand each task to the pool — the work should:
            //       (1) record that one more task has completed
            //       (2) record which thread ran it (think about thread safety)
        }

        // TODO: stop the pool from accepting new work, then wait until all
        //       in-flight tasks have finished before this method returns
    }

    /** Total tasks that have completed. */
    public int getCompletedCount() { return completedCount.get(); }

    /** Names of worker threads that ran tasks (may contain duplicates). */
    public List<String> getWorkerNames() {
        synchronized (workerNames) {
            return List.copyOf(workerNames);
        }
    }

    /** Number of distinct worker thread names (should be ≤ POOL_SIZE). */
    public long getDistinctWorkerCount() {
        synchronized (workerNames) {
            return workerNames.stream().distinct().count();
        }
    }
}
