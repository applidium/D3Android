package com.applidium.pierreferrand.d3library.threading;

public class ValueStorage<T> {
    private T storedValue;
    private final Object synchronisationKey;
    private boolean initialized;

    public ValueStorage() {
        synchronisationKey = new Object();
        initialized = false;
    }

    public ValueStorage(ValueRunnable<T> runnable, Object key) {
        synchronisationKey = new Object();
        setValue(runnable, key);
    }

    public void setValue(ValueRunnable<T> runnable, Object key) {
        initialized = true;
        try {
            synchronized (key) {
                ThreadPool.execute(runnable);
                key.wait();
                storedValue = runnable.getValue();
                synchronized (synchronisationKey) {
                    synchronisationKey.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public T getValue() {
        if (!initialized) {
            throw new IllegalStateException("Not initialized");
        }
        try {
            while (storedValue == null) {
                synchronized (synchronisationKey) {
                    synchronisationKey.wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return storedValue;
    }
}
