package edu.touro.mcon364.concurrency.lesson2.exercises;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Exercise 3 — Limiting concurrent access with {@link Semaphore}.
 *
 * Scenario: a shared printer room has exactly {@code printerCount} printers.
 * Many threads (people) want to print, but only {@code printerCount} may print
 * at the same time.  All others must wait.
 *
 * Your tasks:
 *
 * (A) Implement {@link #print(String)}.
 *     - Acquire one permit from the semaphore before printing.
 *     - Increment {@code activeCount} while printing (to let tests observe
 *       concurrent usage).
 *     - Release the permit in a {@code finally} block.
 *     - Decrement {@code activeCount} after releasing.
 *
 * (B) Observe via tests that {@code maxObservedConcurrency} never exceeds
 *     the number of available printers.
 */
public class PrinterRoom {

    private final int printerCount;
    // TODO: declare a private final Semaphore field
    private final Semaphore semaphore;

    // counters visible to tests
    // active count is the real - time counter. It goes up by 1 right when someone starts printing, and goes down by 1 when they complete.
    private final AtomicInteger activeCount   = new AtomicInteger(0);
    // maxObserved is the high Score or the peak of the room's occupancy. - we used it here to prove the sempahore is working bc if we only hv 3 printers- max should never be 4...
    private final AtomicInteger maxObserved   = new AtomicInteger(0);
    private final AtomicInteger completedJobs = new AtomicInteger(0);

    public PrinterRoom(int printerCount) {
        this.printerCount = printerCount;
        // TODO: initialise the semaphore so that exactly printerCount threads
        //       may be inside print() at the same time
        // we initialize the semaphore with the number of available printers
        this.semaphore = new Semaphore(printerCount);
    }

    /**
     * Simulate printing {@code document}.
     * Must block if all printers are busy.
     *
     * @param document the document to print
     */
    public void print(String document) throws InterruptedException {
        // TODO: block here until a printer permit is available
        semaphore.acquire();
        try {
            // TODO: record that one more job is now active, then update the
            //       high-water mark if the new active count is a new maximum

            // we first increment activeCount
            int current = activeCount.incrementAndGet();

            // then we and update the maxObserved high-water mark if the new active count is a new maximum
            // updateAndGet is a Safe Update Loop. It takes the current value, runs the custom logic on it,
            // and then tries to save the new result—all while making sure no other thread changed the value behind its back
            maxObserved.updateAndGet(prev-> Math.max(prev, current));

            // Simulate printing time
            Thread.sleep(50);

            // record that this job has finished
            completedJobs.incrementAndGet();

            // TODO: record that this job has finished
        } finally { // finally --> no matter what- even if theres a crash- we still release the "token" so no one is blocked out forever
            // TODO: signal that one more printer is free again — do this even
            //       if an exception was thrown, and update the active count
            // we signal that a printer is free again by updating the active count :)
            // We do this in the finally block to ensure the permit is released even if an error occurs
            activeCount.decrementAndGet();
            semaphore.release();
        }
    }

    /** Returns the number of currently active print jobs. */
    public int getActiveCount() { return activeCount.get(); }

    /** Returns the peak number of simultaneous print jobs observed. */
    public int getMaxObservedConcurrency() { return maxObserved.get(); }

    /** Returns the total number of jobs that have completed. */
    public int getCompletedJobs() { return completedJobs.get(); }

    public int getPrinterCount() { return printerCount; }
}
