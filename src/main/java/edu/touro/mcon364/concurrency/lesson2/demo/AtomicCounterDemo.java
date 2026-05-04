package edu.touro.mcon364.concurrency.lesson2.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demo: AtomicInteger vs a plain int field.
 *
 * Key ideas shown:
 *  - A plain {@code int} counter loses updates under concurrency (race condition).
 *  - {@link AtomicInteger#incrementAndGet()} is a single lock-free hardware instruction;
 *    no synchronized keyword is needed.
 *  - {@code compareAndSet()} lets you implement an optimistic update: "apply the
 *    change only if the value is still what I expect it to be."
 */
public class AtomicCounterDemo {

    // ── unsafe counter ──────────────────────────────────────────────────────
    private int unsafeCount = 0;

    /** NOT thread-safe: read-modify-write is not atomic. */
    public void unsafeIncrement() {
        unsafeCount++;          // 3 bytecode ops → another thread can interleave here
    }

    // ── safe counter ────────────────────────────────────────────────────────
    private final AtomicInteger safeCount = new AtomicInteger(0);

    /** Thread-safe: one atomic CPU operation. */
    public void safeIncrement() {
        safeCount.incrementAndGet();
    }

    // ── compare-and-set example ─────────────────────────────────────────────
    private final AtomicInteger sequenceId = new AtomicInteger(1_000);

    /**
     * Reserve the next ID only if the current value is still {@code expected}.
     * Returns the reserved ID, or -1 if the value changed in the meantime.
     */
    public int reserveIdIfStill(int expected) {
        int next = expected + 1;
        boolean succeeded = sequenceId.compareAndSet(expected, next);
        return succeeded ? next : -1;
    }

    // ── main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) throws InterruptedException {
        AtomicCounterDemo demo = new AtomicCounterDemo();
        int threadCount = 20;
        int incPerThread = 5_000;

        // --- unsafe run ---
        List<Thread> unsafeThreads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            unsafeThreads.add(new Thread(() -> {
                for (int j = 0; j < incPerThread; j++) demo.unsafeIncrement();
            }));
        }
        unsafeThreads.forEach(Thread::start);
        for (Thread t : unsafeThreads) t.join();

        // --- safe run ---
        List<Thread> safeThreads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            safeThreads.add(new Thread(() -> {
                for (int j = 0; j < incPerThread; j++) demo.safeIncrement();
            }));
        }
        safeThreads.forEach(Thread::start);
        for (Thread t : safeThreads) t.join();

        int expected = threadCount * incPerThread;
        System.out.printf("Expected  : %,d%n", expected);
        System.out.printf("Unsafe    : %,d  ← likely WRONG%n", demo.unsafeCount);
        System.out.printf("Safe      : %,d  ← always correct%n", demo.safeCount.get());

        // compare-and-set
        System.out.println("\n--- compareAndSet ---");
        System.out.printf("reserveIdIfStill(1000) → %d%n", demo.reserveIdIfStill(1_000));
        System.out.printf("reserveIdIfStill(1000) → %d  ← -1 because value already moved%n",
                demo.reserveIdIfStill(1_000));
    }
}

