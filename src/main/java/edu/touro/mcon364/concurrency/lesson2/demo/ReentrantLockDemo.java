package edu.touro.mcon364.concurrency.lesson2.demo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Demo: {@link ReentrantLock} vs {@code synchronized}.
 *
 * Key ideas shown:
 *  - The lock/finally/unlock idiom — missing the finally is a classic bug.
 *  - {@code tryLock()}: attempt to acquire without blocking forever.
 *  - {@code tryLock(timeout, unit)}: wait up to a time limit, then give up.
 *  - The lock is "reentrant": the same thread can acquire it multiple times.
 */
public class ReentrantLockDemo {

    private final Lock lock = new ReentrantLock();
    private int count = 0;

    // ── pattern 1: basic lock/unlock in finally ──────────────────────────────

    /** Thread-safe increment using the explicit lock. */
    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();   // guaranteed even if an exception is thrown
        }
    }

    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }

    // ── pattern 2: tryLock — do not wait if busy ──────────────────────────────

    /**
     * Tries to increment without blocking.
     *
     * @return {@code true} if the lock was acquired and the counter incremented;
     *         {@code false} if the lock was held by another thread.
     */
    public boolean tryIncrement() {
        if (lock.tryLock()) {
            try {
                count++;
                return true;
            } finally {
                lock.unlock();
            }
        }
        return false;   // someone else held the lock; we moved on
    }

    // ── pattern 3: reentrant — same thread may lock again ────────────────────

    /** Calls increment() from within a block that already holds the lock. */
    public void doubleIncrement() {
        lock.lock();
        try {
            increment();   // same thread acquires lock a second time — allowed
            increment();
        } finally {
            lock.unlock();
        }
    }

    // ── main ──────────────────────────────────────────────────────────────────
    public static void main(String[] args) throws InterruptedException {
        ReentrantLockDemo demo = new ReentrantLockDemo();

        // basic increment from 10 threads
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1_000; j++) demo.increment();
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        System.out.printf("After 10×1 000 increments: %d (expected 10000)%n", demo.getCount());

        // tryIncrement demo
        demo.count = 0;
        boolean got = demo.tryIncrement();
        System.out.printf("tryIncrement succeeded: %b, count = %d%n", got, demo.getCount());

        // doubleIncrement demo
        demo.count = 0;
        demo.doubleIncrement();
        System.out.printf("After doubleIncrement: %d (expected 2)%n", demo.getCount());
    }
}

