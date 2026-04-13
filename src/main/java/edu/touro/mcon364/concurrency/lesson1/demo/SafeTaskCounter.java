package edu.touro.mcon364.concurrency.lesson1.demo;

public class SafeTaskCounter {

    private int count;

    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}
