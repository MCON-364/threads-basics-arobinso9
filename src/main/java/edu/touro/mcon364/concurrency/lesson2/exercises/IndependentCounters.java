package edu.touro.mcon364.concurrency.lesson2.exercises;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Exercise 6 — Replace a coarse object lock with a targeted {@link ReentrantLock}.
 *
 * The class below manages two independent counters: {@code readCount} and
 * {@code writeCount}.  In the original version BOTH methods are {@code synchronized},
 * which means a {@code read()} and a {@code write()} block each other even though
 * they touch completely different state — unnecessary contention.
 *
 * Your tasks:
 *
 * (A) Remove the {@code synchronized} keyword from both methods.
 * (B) Add two separate {@link ReentrantLock} fields — one for reads, one for
 *     writes — and apply the lock/try/finally/unlock pattern in each method.
 * (C) Verify with the tests that:
 *     - Concurrent reads do NOT block each other (both run in parallel).
 *     - The counts are still correct after many concurrent calls.
 *
 * Hint: two threads can hold two different locks at the same time.
 */
public class IndependentCounters {

    private int readCount  = 0;
    private int writeCount = 0;

    // TODO: add two separate lock fields — one to guard readCount, one to guard writeCount.
    //       Why two locks instead of one?  What contention does that eliminate?

    private final Lock readLock = new ReentrantLock();
    private final Lock writeLock = new ReentrantLock();

    /**
     * Record a read operation.
     * TODO: replace the synchronized keyword with an explicit lock.
     *       Remember: always release the lock even if an exception is thrown.
     */
    public void read() {
        readLock.lock(); // we take the read key
        try{
            readCount++;  // we do the work
        } finally{
            readLock.unlock(); // we hv the finally block so even if program crashes - the key will b put back
        }

    }

    /**
     * Record a write operation.
     * TODO: replace the synchronized keyword with an explicit lock.
     *       Remember: always release the lock even if an exception is thrown.
     */
    public synchronized void write() {
        writeLock.lock();
        try{
            writeCount++;
        } finally{
            writeLock.unlock();
        }
    }

    public int getReadCount()  { return readCount; }
    public int getWriteCount() { return writeCount; }
}
