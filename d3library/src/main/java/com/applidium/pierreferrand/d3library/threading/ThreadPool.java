package com.applidium.pierreferrand.d3library.threading;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ThreadPool {
    private static final int MIN_THREADS_NUMBER = 5;
    public static final int CORES_NUMBER = Runtime.getRuntime().availableProcessors();
    private static ExecutorService executor = Executors.newFixedThreadPool(
        Math.max(CORES_NUMBER + 1, MIN_THREADS_NUMBER)
    );
    /**
     * This second thread pool should be used when threads from the first pool launch a blocking
     * call. This avoids deadlocks.
     */
    private static ExecutorService secondaryExecutor = Executors.newFixedThreadPool(
        Math.max(CORES_NUMBER + 1, MIN_THREADS_NUMBER)
    );

    private ThreadPool() {
    }

    public static void execute(List<Callable<Object>> todo) {
        try {
            executor.invokeAll(todo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }


    /**
     * See {@link #secondaryExecutor}.
     */
    public static void executeOnSecondaryPool(List<Callable<Object>> todo) {
        try {
            secondaryExecutor.invokeAll(todo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * See {@link #secondaryExecutor}.
     */
    public static void executeOnSecondaryPool(Runnable runnable) {
        secondaryExecutor.execute(runnable);
    }
}
