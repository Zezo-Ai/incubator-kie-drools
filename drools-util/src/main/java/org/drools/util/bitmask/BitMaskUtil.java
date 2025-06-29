/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.util.bitmask;

public class BitMaskUtil {

    public static boolean intersect(long mask1, long mask2) {
        return (mask1 & mask2) != 0;
    }

    public static long set(long mask, int pos) {
        if (pos < 0) throw new IllegalArgumentException("negative position");
        return mask | (1L << pos);
    }

    public static long reset(long mask, int pos) {
        if (pos < 0) throw new IllegalArgumentException("negative position");
        return mask & (Long.MAX_VALUE - (1L << pos));
    }

    public static boolean isSet(long mask, long bit) {
        return (mask & bit) == bit;
    }

    public static boolean isPositionSet(long mask, int pos) {
        return isSet(mask, 1L << pos);
    }
}
