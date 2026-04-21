package edu.touro.mcon364.concurrency.lesson2.exercises;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Exercise 1 — Refactor a {@code synchronized} counter to {@link AtomicInteger}.
 *
 * The lesson showed that a plain {@code int} field guarded with {@code synchronized}
 * works, but is a blunt tool: the whole method is locked even though the operation
 * is a single value update.
 *
 * Your task:
 *   Replace the {@code synchronized} keyword with an {@link AtomicInteger} so that
 *   the same counter remains thread-safe without using {@code synchronized} at all.
 *
 * Requirements:
 *   - {@code increment()} must be thread-safe using {@code AtomicInteger}.
 *   - {@code decrement()} must be thread-safe using {@code AtomicInteger}.
 *   - {@code getCount()} must return the current value.
 *   - {@code reset()} must set the counter back to zero.
 *   - No {@code synchronized} keyword is allowed anywhere in this class.
 */
public class AtomicTaskCounter {

    // TODO: declare the field that will hold the counter value thread-safely

    /**
     * Atomically increments the counter by one.
     * TODO: update the counter without using synchronized
     */
    public void increment() {
        // TODO: implement
    }

    /**
     * Atomically decrements the counter by one.
     * TODO: update the counter without using synchronized
     */
    public void decrement() {
        // TODO: implement
    }

    /** Returns the current counter value. */
    public int getCount() {
        // TODO: implement
        return 0;
    }

    /** Resets the counter to zero. */
    public void reset() {
        // TODO: implement
    }
}
