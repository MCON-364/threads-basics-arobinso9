package edu.touro.mcon364.concurrency.lesson1.exercises;

/**
 * Exercise 1:
 * Make this class thread-safe so that concurrent increments do not lose updates.
 */
public class ThreadSafeTaskCounter {

    private int count;

    public void increment() {
        // TODO: make this operation thread-safe
        count++;
    }

    public int getCount() {
        // TODO: ensure callers see a correct value under concurrent use
        return count;
    }
}
