package com.applidium.pierreferrand.d3library.threading;

public class ValueStorage<T> {
    private T storedValue;
    private final Object synchronisationKey;
    private final SetValueRunnable runnable;
    private boolean initialized;

    public ValueStorage() {
        synchronisationKey = new Object();
        initialized = false;
        runnable = new SetValueRunnable();
    }

    public void setValue(final ValueRunnable<T> valueRunnable) {
        synchronized (synchronisationKey) {
            initialized = true;
            storedValue = null;
        }
        runnable.setRunnable(valueRunnable);
        ThreadPool.execute(runnable);
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

    private class SetValueRunnable implements Runnable {
        private ValueRunnable<T> runnable;

        private void setRunnable(ValueRunnable<T> runnable) {
            this.runnable = runnable;
        }

        @Override public void run() {
            try {
                runnable.run();
                runnable.getSemaphore().acquire();
                synchronized (ValueStorage.this.synchronisationKey) {
                    storedValue = runnable.getValue();
                    synchronisationKey.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
