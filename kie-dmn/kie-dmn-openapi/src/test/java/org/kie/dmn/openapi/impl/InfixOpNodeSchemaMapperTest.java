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

import java.util.Arrays;
import java.util.List;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.InfixOperator;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.functions.CountFunction;


import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.openapi.impl.SchemaMapperTestUtils.FEEL_NUMBER;
import static org.kie.dmn.openapi.impl.SchemaMapperTestUtils.getBaseNode;
import static org.kie.dmn.openapi.impl.SchemaMapperTestUtils.getSchemaForSimpleType;

class InfixOpNodeSchemaMapperTest {

    @ParameterizedTest
    @EnumSource(InfixOperator.class)
    void populateSchemaFromFunctionInvocationNode(InfixOperator operator) {
        List<Integer> toEnum = Arrays.asList(1, 3, 6, 78);
        toEnum.forEach(rightValue -> {
            Schema toPopulate = getSchemaForSimpleType(null, null, FEEL_NUMBER, BuiltInType.NUMBER);
            String expression = String.format("%s(?) %s %s", CountFunction.INSTANCE.getName(), operator.getSymbol(), rightValue);
            InfixOpNode infixOpNode = getBaseNode(expression, InfixOpNode.class);
            InfixOpNodeSchemaMapper.populateSchemaFromFunctionInvocationNode(toPopulate, infixOpNode);
            Integer expectedMinimum = null;
            Integer expectedMaximum = null;
            switch (operator) {
                case GT -> expectedMinimum = rightValue + 1;
                case GTE -> expectedMinimum = rightValue;
                case LT -> expectedMaximum = rightValue - 1;
                case LTE -> expectedMaximum = rightValue;
                case EQ -> {
                    expectedMinimum = rightValue;
                    expectedMaximum = rightValue;
                }
            }
            assertThat(toPopulate.getMinItems()).isEqualTo(expectedMinimum);
            assertThat(toPopulate.getMaxItems()).isEqualTo(expectedMaximum);
        });
    }
}