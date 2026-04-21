package edu.touro.mcon364.concurrency.lesson2.exercises;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrinterRoomTest {

    @Test
    void allJobsComplete() throws InterruptedException {
        var room = new PrinterRoom(2);
        int jobCount = 10;

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < jobCount; i++) {
            int id = i;
            threads.add(new Thread(() -> {
                try { room.print("doc-" + id); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();

        assertEquals(jobCount, room.getCompletedJobs(),
                "Every submitted job must complete");
    }

    @Test
    void concurrencyNeverExceedsPrinterCount() throws InterruptedException {
        var room = new PrinterRoom(2);
        int jobCount = 12;

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < jobCount; i++) {
            threads.add(new Thread(() -> {
                try { room.print("doc"); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();

        assertTrue(room.getMaxObservedConcurrency() <= room.getPrinterCount(),
                "Peak concurrent jobs must not exceed the number of printers");
    }

    @Test
    void noActiveJobsAfterAllThreadsFinish() throws InterruptedException {
        var room = new PrinterRoom(3);
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            threads.add(new Thread(() -> {
                try { room.print("doc"); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();

        assertEquals(0, room.getActiveCount(),
                "No job should still be active after all threads have finished");
    }
}

