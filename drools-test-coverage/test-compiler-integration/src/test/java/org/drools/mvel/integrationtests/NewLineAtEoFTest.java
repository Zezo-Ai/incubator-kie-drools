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
package org.drools.mvel.integrationtests;

import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message.Level;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests an error appearing when DLR does not contain a new line at the end of
 * the file.
 */
public class NewLineAtEoFTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    private static final String drl =
            "package org.jboss.qa.brms.commentend\n"
                    + "rule simple\n"
                    + "    when\n"
                    + "    then\n"
                    + "        System.out.println(\"Hello world!\");\n"
                    + "end\n";

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testNoNewlineAtTheEnd(KieBaseTestConfiguration kieBaseTestConfiguration) {
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl + "//test");
        if (kieBuilder.getResults().hasMessages(Level.ERROR)) {
            fail(kieBuilder.getResults().getMessages(Level.ERROR).toString());
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testNewlineAtTheEnd(KieBaseTestConfiguration kieBaseTestConfiguration) {
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        assertThat(kieBuilder.getResults().hasMessages(Level.ERROR)).isFalse();
    }
}
