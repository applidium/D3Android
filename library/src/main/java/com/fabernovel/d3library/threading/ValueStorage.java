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
