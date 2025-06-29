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
package org.drools.compiler.integrationtests;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandsTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testSessionTimeCommands(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
            "package org.drools.compiler.integrationtests \n" +
            "import " + Cheese.class.getCanonicalName() + " \n" +
            "rule StringRule \n" +
            "when \n" +
            "    $c : Cheese() \n" +
            "then \n" +
            "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("cep-esp-test", kieBaseTestConfiguration, drl);
        final KieSession kSession = kbase.newKieSession(KieSessionTestConfiguration.STATEFUL_PSEUDO.getKieSessionConfiguration(), null);
        try {
            final KieCommands kieCommands = KieServices.get().getCommands();
            assertThat((long) kSession.execute(kieCommands.newGetSessionTime())).isEqualTo(0L);
            assertThat((long) kSession.execute(kieCommands.newAdvanceSessionTime(2, TimeUnit.SECONDS))).isEqualTo(2000L);
            assertThat((long) kSession.execute(kieCommands.newGetSessionTime())).isEqualTo(2000L);
        } finally {
            kSession.dispose();
        }
    }
}
