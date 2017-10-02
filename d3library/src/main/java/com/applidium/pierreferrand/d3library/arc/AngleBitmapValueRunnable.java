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

package com.applidium.pierreferrand.d3library.arc;

import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.threading.BitmapValueRunnable;

class AngleBitmapValueRunnable<T> extends BitmapValueRunnable {
    @NonNull private final D3Arc<T> arc;

    AngleBitmapValueRunnable(@NonNull D3Arc<T> arc) {
        this.arc = arc;
    }

    @Override protected void computeValue() {
        value.eraseColor(0);
        D3ArcDrawer.drawArcs(
            canvas, arc.innerRadius(), arc.outerRadius(), arc.offsetX(), arc.offsetY(),
            arc.preComputedAngles.getValue(), arc.paint(), arc.colors()
        );
    }
}
