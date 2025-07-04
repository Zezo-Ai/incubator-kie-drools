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
package org.kie.dmn.openapi.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;


public class NamespaceAwareNamingPolicy extends DefaultNamingPolicy {

    private static final Map<String, String> namespacePrefixes = new HashMap<>();

    public NamespaceAwareNamingPolicy(List<DMNModel> dmnModels, String refPrefix) {
        super(refPrefix);
        for (int i = 0; i < dmnModels.size(); i++) {
            namespacePrefixes.put(dmnModels.get(i).getNamespace(), "ns" + (i + 1));
        }
    }

    @Override
    public String getName(DMNType type) {
        return namespacePrefixes.get(type.getNamespace()) + super.getName(type);
    }

}
