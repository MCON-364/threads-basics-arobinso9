package edu.touro.mcon364.concurrency.lesson1.demo;

import java.util.ArrayList;
import java.util.List;

public class UnsafeTaskCounterDemo {

    public static void main(String[] args) throws InterruptedException {
        var counter = new UnsafeTaskCounter();
        int threadCount = 20;
        int incrementsPerThread = 10_000;

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            }, "unsafe-counter-" + i);
            threads.add(t);
        }

        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }

        int expected = threadCount * incrementsPerThread;
        System.out.println("Expected count: " + expected);
        System.out.println("Actual count:   " + counter.getCount());
        System.out.println("Lost updates?   " + (counter.getCount() != expected));
    }
}
