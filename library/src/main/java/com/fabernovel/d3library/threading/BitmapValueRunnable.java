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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;

public abstract class BitmapValueRunnable extends ValueRunnable<Bitmap> {
    @NonNull protected final Canvas canvas;

    protected BitmapValueRunnable() {
        canvas = new Canvas();
        resizeBitmap(1F, 1F);
    }

    public void resizeBitmap(float width, float height) {
        synchronized (key) {
            value = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
            canvas.setBitmap(value);
        }
    }
}
