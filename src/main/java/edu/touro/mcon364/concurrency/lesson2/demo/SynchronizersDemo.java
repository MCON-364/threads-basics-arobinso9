package edu.touro.mcon364.concurrency.lesson2.demo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;

/**
 * Demo: The four synchronizers from Lesson 2, side-by-side.
 *
 * <pre>
 *  CountDownLatch  – a gate that opens once after N signals (one-shot)
 *  Semaphore       – a room with only a fixed number of seats (permit pool)
 *  CyclicBarrier   – a team meeting point at the end of each round (reusable)
 *  Phaser          – a reusable stage manager for several rounds (flexible)
 * </pre>
 *
 * Each demo method is self-contained and can be called independently.
 */
public class SynchronizersDemo {

    // ── 1. CountDownLatch ────────────────────────────────────────────────────

    /**
     * Three worker threads signal that they have finished startup;
     * the main thread waits at the latch until all three have checked in.
     */
    public static void countDownLatchDemo() throws InterruptedException {
        int workerCount = 3;
        CountDownLatch ready = new CountDownLatch(workerCount);

        for (int i = 1; i <= workerCount; i++) {
            int id = i;
            new Thread(() -> {
                System.out.printf("[worker-%d] starting up…%n", id);
                try { Thread.sleep(id * 200L); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                System.out.printf("[worker-%d] ready — calling countDown()%n", id);
                ready.countDown();
            }, "worker-" + i).start();
        }

        System.out.println("[main] waiting for all workers to be ready…");
        ready.await();
        System.out.println("[main] all workers ready — beginning work!");
    }

    // ── 2. Semaphore ─────────────────────────────────────────────────────────

    /**
     * Five threads compete for two permits that model two shared printers.
     * At most two threads "print" simultaneously.
     */
    public static void semaphoreDemo() throws InterruptedException {
        Semaphore printers = new Semaphore(2); // only 2 printers available

        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            int id = i + 1;
            threads[i] = new Thread(() -> {
                try {
                    System.out.printf("[thread-%d] waiting for a printer…%n", id);
                    printers.acquire();
                    System.out.printf("[thread-%d] printing…%n", id);
                    Thread.sleep(400);
                    System.out.printf("[thread-%d] done printing — releasing permit%n", id);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    printers.release();
                }
            }, "thread-" + id);
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
    }

    // ── 3. CyclicBarrier ─────────────────────────────────────────────────────

    /**
     * Three worker threads execute two phases; they all must complete phase 1
     * before any may start phase 2.  The barrier resets automatically.
     */
    public static void cyclicBarrierDemo() throws InterruptedException {
        int parties = 3;
        CyclicBarrier barrier = new CyclicBarrier(parties,
                () -> System.out.println("  *** all parties arrived — advancing to next phase ***"));

        Thread[] workers = new Thread[parties];
        for (int i = 0; i < parties; i++) {
            int id = i + 1;
            workers[i] = new Thread(() -> {
                try {
                    System.out.printf("[worker-%d] phase 1 work%n", id);
                    Thread.sleep(id * 150L);
                    barrier.await();   // wait for the whole team
                    System.out.printf("[worker-%d] phase 2 work%n", id);
                    Thread.sleep(id * 100L);
                    barrier.await();   // barrier resets — reuse it for phase 2 end
                    System.out.printf("[worker-%d] all done%n", id);
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            }, "worker-" + id);
        }

        for (Thread t : workers) t.start();
        for (Thread t : workers) t.join();
    }

    // ── 4. Phaser ────────────────────────────────────────────────────────────

    /**
     * Three parties advance through two phases using a {@link Phaser}.
     * One party deregisters after phase 0 to show dynamic membership.
     */
    public static void phaserDemo() throws InterruptedException {
        Phaser phaser = new Phaser(3);

        for (int i = 1; i <= 3; i++) {
            int id = i;
            new Thread(() -> {
                System.out.printf("[party-%d] phase 0 work%n", id);
                phaser.arriveAndAwaitAdvance();  // end of phase 0

                if (id == 3) {
                    // party 3 leaves after phase 0
                    System.out.printf("[party-%d] deregistering — work done after phase 0%n", id);
                    phaser.arriveAndDeregister();
                    return;
                }

                System.out.printf("[party-%d] phase 1 work%n", id);
                phaser.arriveAndAwaitAdvance();  // end of phase 1
                System.out.printf("[party-%d] all phases complete%n", id);
            }, "party-" + id).start();
        }
    }

    // ── main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== CountDownLatch demo ===");
        countDownLatchDemo();
        Thread.sleep(200);

        System.out.println("\n=== Semaphore demo ===");
        semaphoreDemo();
        Thread.sleep(200);

        System.out.println("\n=== CyclicBarrier demo ===");
        cyclicBarrierDemo();
        Thread.sleep(200);

        System.out.println("\n=== Phaser demo ===");
        phaserDemo();
        Thread.sleep(1_000);
    }
}

