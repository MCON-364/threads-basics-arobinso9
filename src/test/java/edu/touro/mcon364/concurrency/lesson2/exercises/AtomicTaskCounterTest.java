package edu.touro.mcon364.concurrency.lesson2.exercises;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AtomicTaskCounterTest {

    @Test
    void incrementsCorrectlyUnderConcurrency() throws InterruptedException {
        var counter = new AtomicTaskCounter();
        int threadCount = 20;
        int incPerThread = 5_000;

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < incPerThread; j++) counter.increment();
            }));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();

        assertEquals(threadCount * incPerThread, counter.getCount(),
                "All increments must be visible with no lost updates");
    }

    @Test
    void decrementsCorrectlyUnderConcurrency() throws InterruptedException {
        var counter = new AtomicTaskCounter();
        int threads = 10;
        int ops = 1_000;

        // Increment first so we can decrement without going negative
        for (int i = 0; i < threads * ops; i++) counter.increment();

        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            ts.add(new Thread(() -> {
                for (int j = 0; j < ops; j++) counter.decrement();
            }));
        }
        ts.forEach(Thread::start);
        for (Thread t : ts) t.join();

        assertEquals(0, counter.getCount());
    }

    @Test
    void resetSetsCountToZero() throws InterruptedException {
        var counter = new AtomicTaskCounter();
        for (int i = 0; i < 50; i++) counter.increment();
        counter.reset();
        assertEquals(0, counter.getCount());
    }

    @Test
    void getCountReflectsCurrentState() {
        var counter = new AtomicTaskCounter();
        assertEquals(0, counter.getCount());
        counter.increment();
        counter.increment();
        assertEquals(2, counter.getCount());
        counter.decrement();
        assertEquals(1, counter.getCount());
    }
}

