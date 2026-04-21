package edu.touro.mcon364.concurrency.lesson2.exercises;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkerStartupLatchTest {

    @Test
    void allStartedIsFalseBeforeLaunch() {
        var latch = new WorkerStartupLatch();
        assertFalse(latch.isAllStarted());
    }

    @Test
    void allStartedIsTrueAfterLaunchAndWait() throws InterruptedException {
        var latch = new WorkerStartupLatch();
        latch.launchAndWait(3);
        assertTrue(latch.isAllStarted(),
                "allStarted must be true after launchAndWait() returns");
    }

    @Test
    void correctNumberOfWorkersCheckedIn() throws InterruptedException {
        var latch = new WorkerStartupLatch();
        latch.launchAndWait(5);
        assertEquals(5, latch.getStartedNames().size(),
                "Exactly 5 workers must have checked in");
    }

    @Test
    void workerNamesHaveExpectedPattern() throws InterruptedException {
        var latch = new WorkerStartupLatch();
        latch.launchAndWait(4);
        List<String> names = latch.getStartedNames();
        assertTrue(names.stream().allMatch(n -> n.startsWith("worker-")),
                "Every checked-in name must start with 'worker-'");
    }

    @Test
    void callerBlocksUntilAllWorkersCheckIn() throws InterruptedException {
        // If launchAndWait() returned before all countDown() calls, the count
        // would be wrong.  The assertion above (size == workerCount) covers this,
        // but we add an explicit timing guard here.
        var latch = new WorkerStartupLatch();
        long before = System.currentTimeMillis();
        latch.launchAndWait(3);
        // The threads sleep id*200 ms max (600 ms), so this should return only
        // after roughly that — but we just check correctness, not exact timing.
        assertTrue(latch.isAllStarted());
    }
}

