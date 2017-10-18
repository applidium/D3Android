/*
 * Copyright 2017, Fabernovel Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fabernovel.d3library.threading;

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
