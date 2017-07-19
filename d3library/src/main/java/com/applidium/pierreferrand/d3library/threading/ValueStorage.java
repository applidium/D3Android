package com.applidium.pierreferrand.d3library.threading;

public class ValueStorage<T> {
    private T storedValue;
    private final Object synchronisationKey;
    private boolean initialized;

    public ValueStorage() {
        synchronisationKey = new Object();
        initialized = false;
    }

    public void setValue(final ValueRunnable<T> runnable) {
        synchronized (synchronisationKey) {
            initialized = true;
            storedValue = null;
        }
        ThreadPool.execute(new Runnable() {
            @Override public void run() {
                try {
                    runnable.run();
                    runnable.getSemaphore().acquire();
                    synchronized (synchronisationKey) {
                        storedValue = runnable.getValue();
                        synchronisationKey.notifyAll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public T getValue() {
        try {
            synchronized (synchronisationKey) {
                if (!initialized) {
                    throw new IllegalStateException("Not initialized");
                }
                while (storedValue == null) {
                    synchronisationKey.wait();
                }
                return storedValue;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
