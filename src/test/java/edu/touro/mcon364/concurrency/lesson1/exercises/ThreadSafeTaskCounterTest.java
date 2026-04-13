package edu.touro.mcon364.concurrency.lesson1.exercises;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThreadSafeTaskCounterTest {

    @Test
    void incrementsCorrectlyUnderConcurrency() throws InterruptedException {
        var counter = new ThreadSafeTaskCounter();
        int threadCount = 20;
        int incrementsPerThread = 5_000;

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });
            threads.add(t);
        }

        threads.forEach(Thread::start);
        for (Thread t : threads) {
            t.join();
        }

        assertEquals(threadCount * incrementsPerThread, counter.getCount());
    }
}
