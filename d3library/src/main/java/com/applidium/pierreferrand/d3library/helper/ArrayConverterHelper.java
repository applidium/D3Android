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

package com.applidium.pierreferrand.d3library.helper;

import android.support.annotation.NonNull;

public final class ArrayConverterHelper {
    private static final String CONTAINER_ARRAY_IS_TOO_SMALL = "Container array is too small";

    private ArrayConverterHelper(){}

    public static float[] convertArray(@NonNull Float[] toConvert, @NonNull float[] resContainer) {
        if (resContainer.length < toConvert.length) {
            throw new IllegalStateException(CONTAINER_ARRAY_IS_TOO_SMALL);
        }
        for (int i = 0; i < toConvert.length; i++) {
            resContainer[i] = toConvert[i] == null ? 0F : toConvert[i];
        }
        return resContainer;
    }
}
