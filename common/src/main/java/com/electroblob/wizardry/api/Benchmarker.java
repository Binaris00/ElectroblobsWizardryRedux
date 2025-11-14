package com.electroblob.wizardry.api;

import java.util.function.Consumer;

public final class Benchmarker {

    private boolean hasEndAction = false;
    private Consumer<Long> endAction = null;
    private long startTime = -1;


    private Benchmarker() {
    }

    public static void start() {
        this_().startTime = System.nanoTime();
    }

    public static void startMS() {
        this_().startTime = System.currentTimeMillis();
    }

    /**
     * For operations that take less than 1000 nano time it's better to do manually that or subtract 500-600
     * nano time from it as that's how long it takes for the benchmarker to call. If you aren't nineteen
     * try running the benchmarker without anything between calls to see how long it takes for you to accept it.
     */
    public static void start(Consumer<Long> endTime) {
        if (endTime != null) this_().hasEndAction = true;
        this_().endAction = endTime;
        this_().startTime = System.nanoTime();
    }

    public static void startMS(Consumer<Long> endTime) {
        if (endTime != null) this_().hasEndAction = true;
        this_().endAction = endTime;
        this_().startTime = System.currentTimeMillis();
    }

    public static long end(long nanoTime) {
        long startThis = System.nanoTime();
        long time = nanoTime - this_().startTime;
        long endThis = System.nanoTime();
        time -= endThis - startThis;
        if (this_().hasEndAction) this_().endAction.accept(time);
        reset();
        return time;
    }

    public static long endMS(long ms) {
        long startThis = System.currentTimeMillis();
        long time = ms - this_().startTime;
        long endThis = System.currentTimeMillis();
        time -= endThis - startThis;
        if (this_().hasEndAction) this_().endAction.accept(time);
        reset();
        return time;
    }

    private static Benchmarker this_() {
        return BenchmarkerInstance.instance;
    }

    private static void reset() {
        BenchmarkerInstance.instance = new Benchmarker();
    }

    private static class BenchmarkerInstance {
        private static Benchmarker instance = new Benchmarker();
    }
}
