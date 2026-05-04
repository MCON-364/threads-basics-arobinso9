package edu.touro.mcon364.concurrency.lesson2.exercises;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ParallelSumCalculatorTest {

    @Test
    void sumOf1To100() throws InterruptedException, ExecutionException {
        var calc = new ParallelSumCalculator();
        List<Integer> numbers = IntStream.rangeClosed(1, 100)
                .boxed().toList();
        long result = calc.parallelSum(numbers, 4);
        assertEquals(5050L, result);
    }

    @Test
    void sumMatchesSequentialSum() throws InterruptedException, ExecutionException {
        var calc = new ParallelSumCalculator();
        List<Integer> numbers = IntStream.rangeClosed(1, 1_000)
                .boxed().toList();
        long expected = numbers.stream().mapToLong(Integer::longValue).sum();
        long result = calc.parallelSum(numbers, 5);
        assertEquals(expected, result);
    }

    @Test
    void singleWorker() throws InterruptedException, ExecutionException {
        var calc = new ParallelSumCalculator();
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        assertEquals(15L, calc.parallelSum(numbers, 1));
    }

    @Test
    void moreWorkersThanElements() throws InterruptedException, ExecutionException {
        var calc = new ParallelSumCalculator();
        List<Integer> numbers = List.of(10, 20, 30);
        assertEquals(60L, calc.parallelSum(numbers, 10));
    }
}

