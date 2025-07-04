/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.parser.antlr4;

import java.util.List;

import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.drl.parser.antlr4.DRL10ParserHelper.computeTokenIndex;
import static org.drools.drl.parser.antlr4.DRL10ParserHelper.createDrlParser;
import static org.drools.drl.parser.antlr4.DRL10ParserHelper.parse;

/**
 * This test class is specific to new antlr4 parser.
 */
class DRL10ParserTest {

    private static final String drl =
            "package org.test;\n" +
                    "import org.test.model.Person;\n" +
                    "global String result;\n" +
                    "rule TestRule @Test(true) no-loop salience 15 when \n" +
                    "  $p:Person( age >= 18 )\n" +
                    "then\n" +
                    "  int a = 4;\n" +
                    "  System.out.println($p.getName());\n" +
                    "end\n";

    @Test
    void parse_basicRule() {
        PackageDescr packageDescr = parse(drl);
        assertThat(packageDescr.getName()).isEqualTo("org.test");

        assertThat(packageDescr.getImports()).hasSize(1);
        assertThat(packageDescr.getImports().get(0).getTarget()).isEqualTo("org.test.model.Person");

        assertThat(packageDescr.getGlobals()).hasSize(1);
        GlobalDescr globalDescr = packageDescr.getGlobals().get(0);
        assertThat(globalDescr.getType()).isEqualTo("String");
        assertThat(globalDescr.getIdentifier()).isEqualTo("result");

        assertThat(packageDescr.getRules()).hasSize(1);
        RuleDescr ruleDescr = packageDescr.getRules().get(0);

        AnnotationDescr annotationDescr = ruleDescr.getAnnotation("Test");
        assertThat(annotationDescr).isNotNull();
        assertThat(annotationDescr.getValue()).isEqualTo("true");

        assertThat(ruleDescr.getAttributes()).hasSize(2);
        assertThat(ruleDescr.getAttributes().get("no-loop")).isNotNull();
        AttributeDescr salience = ruleDescr.getAttributes().get("salience");
        assertThat(salience).isNotNull();
        assertThat(salience.getValue()).isEqualTo("15");

        assertThat(ruleDescr.getName()).isEqualTo("TestRule");

        assertThat(ruleDescr.getLhs().getDescrs()).hasSize(1);
        PatternDescr patternDescr = (PatternDescr) ruleDescr.getLhs().getDescrs().get(0);
        assertThat(patternDescr.getIdentifier()).isEqualTo("$p");
        assertThat(patternDescr.getObjectType()).isEqualTo("Person");

        List<? extends BaseDescr> constraints = patternDescr.getConstraint().getDescrs();
        assertThat(constraints).hasSize(1);
        ExprConstraintDescr expr = (ExprConstraintDescr) constraints.get(0);
        assertThat(expr.getExpression()).isEqualTo("age >= 18");

        assertThat(ruleDescr.getConsequence().toString()).isEqualToIgnoringWhitespace("int a = 4; System.out.println($p.getName());");
    }

    @Test
    void computeTokenIndex_basicRule() {
        DRL10Parser parser = createDrlParser(drl);
        parser.compilationUnit();

        assertThat((int) computeTokenIndex(parser, 1, 0)).isEqualTo(0);
        assertThat((int) computeTokenIndex(parser, 1, 1)).isEqualTo(0);
        assertThat((int) computeTokenIndex(parser, 1, 7)).isEqualTo(0);
        assertThat((int) computeTokenIndex(parser, 1, 8)).isEqualTo(1);
        assertThat((int) computeTokenIndex(parser, 1, 9)).isEqualTo(2);
        assertThat((int) computeTokenIndex(parser, 1, 9)).isEqualTo(2);
        assertThat((int) computeTokenIndex(parser, 1, 12)).isEqualTo(3);
        assertThat((int) computeTokenIndex(parser, 1, 13)).isEqualTo(4);
        assertThat((int) computeTokenIndex(parser, 1, 17)).isEqualTo(5);
        assertThat((int) computeTokenIndex(parser, 1, 18)).isEqualTo(6);
        assertThat((int) computeTokenIndex(parser, 2, 0)).isEqualTo(6);
        assertThat((int) computeTokenIndex(parser, 2, 1)).isEqualTo(7);
        assertThat((int) computeTokenIndex(parser, 2, 6)).isEqualTo(7);
        assertThat((int) computeTokenIndex(parser, 2, 7)).isEqualTo(8);
        // Skip RHS token assertion as it is fluid part at the moment.
    }
}
