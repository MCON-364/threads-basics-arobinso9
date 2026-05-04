package edu.touro.mcon364.concurrency.lesson2.exercises;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IndependentCountersTest {

    @Test
    void readCountIsCorrectUnderConcurrency() throws InterruptedException {
        var counters = new IndependentCounters();
        int threads = 10;
        int ops = 2_000;

        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            ts.add(new Thread(() -> {
                for (int j = 0; j < ops; j++) counters.read();
            }));
        }
        ts.forEach(Thread::start);
        for (Thread t : ts) t.join();

        assertEquals(threads * ops, counters.getReadCount(),
                "No read increments must be lost");
    }

    @Test
    void writeCountIsCorrectUnderConcurrency() throws InterruptedException {
        var counters = new IndependentCounters();
        int threads = 10;
        int ops = 2_000;

        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            ts.add(new Thread(() -> {
                for (int j = 0; j < ops; j++) counters.write();
            }));
        }
        ts.forEach(Thread::start);
        for (Thread t : ts) t.join();

        assertEquals(threads * ops, counters.getWriteCount(),
                "No write increments must be lost");
    }

    @Test
    void readAndWriteCountsAreIndependent() throws InterruptedException {
        var counters = new IndependentCounters();
        Thread reader = new Thread(() -> { for (int i = 0; i < 100; i++) counters.read(); });
        Thread writer = new Thread(() -> { for (int i = 0; i < 200; i++) counters.write(); });
        reader.start(); writer.start();
        reader.join();  writer.join();

        assertEquals(100, counters.getReadCount());
        assertEquals(200, counters.getWriteCount());
    }
}

