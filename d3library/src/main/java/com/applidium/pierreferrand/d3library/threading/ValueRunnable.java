package com.applidium.pierreferrand.d3library.threading;

import android.support.annotation.NonNull;

import java.util.concurrent.Semaphore;

public abstract class ValueRunnable<T> {
    @NonNull protected final Semaphore semaphore = new Semaphore(0, true);
    @NonNull private final Object key = new Object();

    protected T value;

    public T getValue() {
        return value;
    }

    public final Semaphore getSemaphore() {
        return semaphore;
    }

    public final void run() {
        synchronized (key) {
            computeValue();
            semaphore.release();
        }
    }

    protected abstract void computeValue();
}
