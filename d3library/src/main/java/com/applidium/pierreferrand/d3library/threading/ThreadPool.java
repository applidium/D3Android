package com.applidium.pierreferrand.d3library.threading;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    public final static int coresNumber = Runtime.getRuntime().availableProcessors();
    private static ExecutorService executor = Executors.newFixedThreadPool(coresNumber);

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
}
