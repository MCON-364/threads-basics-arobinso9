package edu.touro.mcon364.concurrency.lesson2.exercises;

import java.util.concurrent.CountDownLatch;

// latch waits till all threads completed b4 moving on
// It ensures that your main program doesn't start doing Job B until Job A has been prepared by several different threads.
// It maintains a counter that you set at the beginning. It has two main functions:
// countDown(): Decrements the counter (workers call this).- meaning we call it inside the run method of the new thread
// await(): Blocks the current thread until the counter reaches zero (the manager calls this).
/**
 * Exercise 2 — Startup coordination with {@link CountDownLatch}.
 *
 * Scenario: a coordinator thread must wait until a fixed number of worker
 * threads have finished their startup phase before allowing work to begin.
 *
 * Your tasks:
 *
 * (A) Implement {@link #launchAndWait(int)}.
 *     - Create a {@code CountDownLatch} with the given {@code workerCount}.
 *     - Start {@code workerCount} threads.  Each thread must:
 *         1. Record its name in the {@code startedNames} list (use a
 *            {@code synchronized} block on the list).
 *         2. Call {@code countDown()} on the latch.
 *     - After starting all threads, call {@code latch.await()} on the main
 *       calling thread so it blocks until all workers have checked in.
 *     - After {@code await()} returns, set {@code allStarted = true}.
 *
 * (B) Understand the difference from a plain {@code join()} loop:
 *     A {@code CountDownLatch} lets the workers keep running after they
 *     signal; {@code join()} would wait for the thread to fully terminate.
 */
public class WorkerStartupLatch {

    // Written by launchAndWait()
    private volatile boolean allStarted = false;
    private final java.util.List<String> startedNames =
            new java.util.ArrayList<>();

    /**
     * Launch {@code workerCount} threads, wait for all to check in, then set
     * {@code allStarted = true}.
     *
     * @param workerCount number of worker threads to create
     */

    public void launchAndWait(int workerCount) throws InterruptedException {
        // TODO: create a latch that will count down once per worker
        CountDownLatch latch = new CountDownLatch(workerCount);

            // TODO: create and start a thread named "worker-" + id that:
            //       (1) records its own name in startedNames (think about thread safety here)
            //       (2) signals the latch that it is ready

        for (int i = 1; i <= workerCount; i++) {
            String threadName = "worker-" + i;

            Thread worker = new Thread(() -> {
                // record name safely - we lock on the arrayList- startedNames and then we modify.
                // since ArrayList isn't thread-safe, we must wrap the add in a synchronized block.
                synchronized (startedNames) {
                    startedNames.add(Thread.currentThread().getName());
                }
                // signal the latch
                latch.countDown();
            }, threadName);

            worker.start();
        }

        // TODO: make the calling thread wait here until every worker has signalled
        // we block the main thread until the count reaches 0
        latch.await();

        // TODO: mark the startup phase as complete
        allStarted = true;
    }

    /** Returns {@code true} once all workers have called {@code countDown()}. */
    public boolean isAllStarted() {
        return allStarted;
    }

    /** Returns the names of threads that checked in (order may vary). */
    public java.util.List<String> getStartedNames() {
        return java.util.List.copyOf(startedNames);
    }
}
