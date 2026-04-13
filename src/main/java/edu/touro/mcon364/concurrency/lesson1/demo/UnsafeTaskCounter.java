package edu.touro.mcon364.concurrency.lesson1.demo;

public class UnsafeTaskCounter {

    private int count;

    public void increment() {
        int current = count;
        if ((current & 255) == 0) {
            Thread.yield();
        }
        count = current + 1; // intentionally unsafe read-modify-write
    }

    public int getCount() {
        return count;
    }
}
