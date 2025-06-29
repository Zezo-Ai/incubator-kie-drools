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
package org.drools.core.util;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DroolsAssert {

    public static void assertEnumerationSize(int expectedSize, Enumeration<?> enumeration) {
        int actualSize = 0;
        while (enumeration.hasMoreElements()) {
            enumeration.nextElement();
            actualSize++;
        }
        assertThat(actualSize).as("Enumeration size different than expected.").isEqualTo(expectedSize);
    }

    public static void assertUrlEnumerationContainsMatch(String regex, Enumeration<URL> enumeration) {
        List<URL> list = Collections.list(enumeration);
        for (URL url : list) {
            if (url.toExternalForm().matches(regex)) {
                return;
            }
        }
        throw new AssertionError("The enumeration (" + list
                + ") does not contain an URL that matches regex (" + regex + ").");
    }

    private DroolsAssert() {
    }

}
