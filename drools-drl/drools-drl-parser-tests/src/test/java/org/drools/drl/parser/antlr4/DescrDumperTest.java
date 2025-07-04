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
package org.drools.drl.parser.antlr4;

import org.drools.compiler.lang.DescrDumper;
import org.drools.compiler.lang.DumperContext;
import org.drools.drl.ast.descr.AtomicExprDescr;
import org.drools.drl.ast.descr.BindingDescr;
import org.drools.drl.ast.descr.ConstraintConnectiveDescr;
import org.drools.drl.parser.DrlExprParser;
import org.drools.mvel.evaluators.MatchesEvaluatorsDefinition;
import org.drools.mvel.evaluators.SetEvaluatorsDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import static org.assertj.core.api.Assertions.assertThat;

public class DescrDumperTest {

    private DescrDumper dumper;

    @BeforeEach
    void setUp() {
        // configure operators
        new SetEvaluatorsDefinition();
        new MatchesEvaluatorsDefinition();

        dumper = new DescrDumper();
    }

    @DisabledIfSystemProperty(named = "drools.drl.antlr4.parser.enabled", matches = "true")
    @Test
    void dump() {
        String input = "price > 10 && < 20 || == $val || == 30";
        String expected = "( price > 10 && price < 20 || price == $val || price == 30 )";

        ConstraintConnectiveDescr descr = parse(input);
        String result = dumper.dump(descr);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void dumpMatches() {
        String input = "type.toString matches \"something\\swith\\tsingle escapes\"";
        String expected = "type.toString ~= \"something\\swith\\tsingle escapes\"";

        ConstraintConnectiveDescr descr = parse(input);
        String result = dumper.dump(descr);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void dumpMatches2() {
        String input = "type.toString matches 'something\\swith\\tsingle escapes'";
        String expected = "type.toString ~= \"something\\swith\\tsingle escapes\"";

        ConstraintConnectiveDescr descr = parse(input);
        String result = dumper.dump(descr);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void dumpMatches3() {
        String input = "this[\"content\"] matches \"hello ;=\"";
        String expected = "this[\"content\"] ~= \"hello ;=\"";

        ConstraintConnectiveDescr descr = parse(input);
        String result = dumper.dump(descr);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void dumpContains() {
        String input = "list contains \"b\"";
        String expected = "list contains \"b\"";

        ConstraintConnectiveDescr descr = parse(input);
        String result = dumper.dump(descr);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void dumpContains2() {
        String input = "list not contains \"b\"";
        String expected = "!( list contains \"b\" )";

        ConstraintConnectiveDescr descr = parse(input);
        String result = dumper.dump(descr);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void dumpExcludes() {
        String input = "list excludes \"b\"";
        String expected = "!( list contains \"b\" )";

        ConstraintConnectiveDescr descr = parse(input);
        String result = dumper.dump(descr);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void dumpExcludes2() {
        String input = "list not excludes \"b\"";
        String expected = "list contains \"b\"";

        ConstraintConnectiveDescr descr = parse(input);
        String result = dumper.dump(descr);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @Disabled
    void dumpWithDateAttr() {
        String input = "son.birthDate == \"01-jan-2000\"";
        String expected = "son.birthDate == org.drools.util.DateUtils.parseDate( \"01-jan-2000\" )";

        ConstraintConnectiveDescr descr = parse(input);
        String result = dumper.dump(descr);

        assertThat(result).isEqualTo(expected);
    }

    @DisabledIfSystemProperty(named = "drools.drl.antlr4.parser.enabled", matches = "true")
    @Test
    void dumpComplex() {
        String input = "a ( > 60 && < 70 ) || ( > 50 && < 55 ) && a3 == \"black\" || a == 40 && a3 == \"pink\" || a == 12 && a3 == \"yellow\" || a3 == \"blue\"";
        String expected = "( ( a > 60 && a < 70 || a > 50 && a < 55 ) && a3 == \"black\" || a == 40 && a3 == \"pink\" || a == 12 && a3 == \"yellow\" || a3 == \"blue\" )";

        ConstraintConnectiveDescr descr = parse(input);
        String result = dumper.dump(descr);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void dumpBindings() {
        String input = "$x : property > value";
        String expected = "property > value";

        ConstraintConnectiveDescr descr = parse(input);
        DumperContext ctx = new DumperContext();
        String result = dumper.dump(descr,
                                    ctx);

        assertThat(result).isEqualTo(expected);
        assertThat(ctx.getBindings()).hasSize(1);
        BindingDescr bind = ctx.getBindings().get(0);
        assertThat(bind.getVariable()).isEqualTo("$x");
        assertThat(bind.getExpression()).isEqualTo("property");
    }

    @Test
    void dumpBindings2() {
        String input = "( $a : a > $b : b[10].prop || 10 != 20 ) && $x : someMethod(10) == 20";
        String expected = "( a > b[10].prop || 10 != 20 ) && someMethod(10) == 20";

        ConstraintConnectiveDescr descr = parse(input);
        DumperContext ctx = new DumperContext();
        String result = dumper.dump(descr,
                                    ctx);

        assertThat(result).isEqualTo(expected);
        assertThat(ctx.getBindings()).hasSize(3);
        BindingDescr bind = ctx.getBindings().get(0);
        assertThat(bind.getVariable()).isEqualTo("$a");
        assertThat(bind.getExpression()).isEqualTo("a");
        bind = ctx.getBindings().get(1);
        assertThat(bind.getVariable()).isEqualTo("$b");
        assertThat(bind.getExpression()).isEqualTo("b[10].prop");
        bind = ctx.getBindings().get(2);
        assertThat(bind.getVariable()).isEqualTo("$x");
        assertThat(bind.getExpression()).isEqualTo("someMethod(10)");
    }

    @Test
    void dumpBindings3() {
        String input = "( $a : a > $b : b[10].prop || 10 != 20 ) && $x : someMethod(10)";
        String expected = "( a > b[10].prop || 10 != 20 )";

        ConstraintConnectiveDescr descr = parse(input);
        String result = dumper.dump(descr);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void dumpBindings4() {
        String input = "( $a : a > $b : b[10].prop || $x : someMethod(10) ) && 10 != 20";
        String expected = "( a > b[10].prop ) && 10 != 20";

        ConstraintConnectiveDescr descr = parse(input);
        String result = dumper.dump(descr);

        assertThat(result).isEqualTo(expected);
    }

    @DisabledIfSystemProperty(named = "drools.drl.antlr4.parser.enabled", matches = "true")
    @Test
    void dumpBindingsWithRestriction() {
        String input = "$x : age > 10 && < 20 || > 30";
        String expected = "( age > 10 && age < 20 || age > 30 )";

        ConstraintConnectiveDescr descr = parse(input);
        DumperContext ctx = new DumperContext();
        String result = dumper.dump(descr,
                                    ctx);

        assertThat(result).isEqualTo(expected);
        assertThat(ctx.getBindings()).hasSize(1);
        BindingDescr bind = ctx.getBindings().get(0);
        assertThat(bind.getVariable()).isEqualTo("$x");
        assertThat(bind.getExpression()).isEqualTo("age");
    }

    @Test
    void dumpBindingsComplexOp() {
        String input = "$x : age in (10, 20, $someVal)";
        String expected = "( age == 10 || age == 20 || age == $someVal )";

        ConstraintConnectiveDescr descr = parse(input);
        DumperContext ctx = new DumperContext();
        String result = dumper.dump(descr,
                                    ctx);

        assertThat(result).isEqualTo(expected);
        assertThat(ctx.getBindings()).hasSize(1);
        BindingDescr bind = ctx.getBindings().get(0);
        assertThat(bind.getVariable()).isEqualTo("$x");
        assertThat(bind.getExpression()).isEqualTo("age");
    }

    @Test
    void dumpBindingsComplexOp2() {
        String input = "$x : age not in (10, 20, $someVal)";
        String expected = "age != 10 && age != 20 && age != $someVal";

        ConstraintConnectiveDescr descr = parse(input);
        DumperContext ctx = new DumperContext();
        String result = dumper.dump(descr,
                                    ctx);

        assertThat(result).isEqualTo(expected);
        assertThat(ctx.getBindings()).hasSize(1);
        BindingDescr bind = ctx.getBindings().get(0);
        assertThat(bind.getVariable()).isEqualTo("$x");
        assertThat(bind.getExpression()).isEqualTo("age");
    }

    @Test
    void processInlineCast() {
        String expr = "field1#Class.field2";
        String expectedInstanceof = "field1 instanceof Class";
        String expectedcasted = "((Class)field1).field2";
        AtomicExprDescr atomicExpr = new AtomicExprDescr(expr);
        ConstraintConnectiveDescr ccd = new ConstraintConnectiveDescr();
        ccd.addDescr(atomicExpr);
        String[] instanceofAndCastedExpr = dumper.processImplicitConstraints(expr, atomicExpr, ccd, ccd.getDescrs().indexOf(atomicExpr), null);
        assertThat(ccd.getDescrs()).hasSize(2);
        assertThat(ccd.getDescrs().get(0).toString()).isEqualTo(expectedInstanceof);
        assertThat(atomicExpr.getRewrittenExpression()).isEqualTo(expectedcasted);

        expr = "field1#Class1.field2#Class2.field3";
        String expectedInstanceof1 = "field1 instanceof Class1";
        String expectedInstanceof2 = "((Class1)field1).field2 instanceof Class2";
        expectedcasted = "((Class2)((Class1)field1).field2).field3";
        atomicExpr = new AtomicExprDescr(expr);
        ccd = new ConstraintConnectiveDescr();
        instanceofAndCastedExpr = dumper.processImplicitConstraints(expr, atomicExpr, ccd, ccd.getDescrs().indexOf(atomicExpr), null);
        assertThat(ccd.getDescrs().get(0).toString()).isEqualTo(expectedInstanceof1);
        assertThat(ccd.getDescrs().get(1).toString()).isEqualTo(expectedInstanceof2);
        assertThat(instanceofAndCastedExpr[1]).isEqualTo(expectedcasted);
        assertThat(atomicExpr.getRewrittenExpression()).isEqualTo(expectedcasted);
    }

    @Test
    void processNullSafeDereferencing() {
        String expr = "field1!.field2";
        String expectedNullCheck = "field1 != null";
        String expectedExpr = "field1.field2";
        AtomicExprDescr atomicExpr = new AtomicExprDescr(expr);
        ConstraintConnectiveDescr ccd = new ConstraintConnectiveDescr();
        String[] nullCheckAndExpr = dumper.processImplicitConstraints(expr, atomicExpr, ccd, ccd.getDescrs().indexOf(atomicExpr), null);
        assertThat(ccd.getDescrs().get(0).toString()).isEqualTo(expectedNullCheck);
        assertThat(nullCheckAndExpr[1]).isEqualTo(expectedExpr);
        assertThat(atomicExpr.getRewrittenExpression()).isEqualTo(expectedExpr);

        expr = "field1!.field2!.field3";
        String expectedNullCheck1 = "field1 != null";
        String expectedNullCheck2 = "field1.field2 != null";
        expectedExpr = "field1.field2.field3";
        atomicExpr = new AtomicExprDescr(expr);
        ccd = new ConstraintConnectiveDescr();
        nullCheckAndExpr = dumper.processImplicitConstraints(expr, atomicExpr, ccd, ccd.getDescrs().indexOf(atomicExpr), null);
        assertThat(ccd.getDescrs().get(0).toString()).isEqualTo(expectedNullCheck1);
        assertThat(ccd.getDescrs().get(1).toString()).isEqualTo(expectedNullCheck2);
        assertThat(nullCheckAndExpr[1]).isEqualTo(expectedExpr);
        assertThat(atomicExpr.getRewrittenExpression()).isEqualTo(expectedExpr);
    }

    @Test
    void processImplicitConstraints() {
        String expr = "field1#Class!.field2";
        String expectedConstraints = "field1 instanceof Class";
        String expectedExpr = "((Class)field1).field2";
        AtomicExprDescr atomicExpr = new AtomicExprDescr(expr);
        ConstraintConnectiveDescr ccd = new ConstraintConnectiveDescr();
        String[] constraintsAndExpr = dumper.processImplicitConstraints(expr, atomicExpr, ccd, ccd.getDescrs().indexOf(atomicExpr), null);
        assertThat(ccd.getDescrs().get(0).toString()).isEqualTo(expectedConstraints);
        assertThat(constraintsAndExpr[1]).isEqualTo(expectedExpr);
        assertThat(atomicExpr.getRewrittenExpression()).isEqualTo(expectedExpr);

        expr = "field1!.field2#Class.field3";
        String expectedConstraints1 = "field1 != null";
        String expectedConstraints2 = "field1.field2 instanceof Class";
        expectedExpr = "((Class)field1.field2).field3";
        atomicExpr = new AtomicExprDescr(expr);
        ccd = new ConstraintConnectiveDescr();
        constraintsAndExpr = dumper.processImplicitConstraints(expr, atomicExpr, ccd, ccd.getDescrs().indexOf(atomicExpr), null);
        assertThat(ccd.getDescrs().get(0).toString()).isEqualTo(expectedConstraints1);
        assertThat(ccd.getDescrs().get(1).toString()).isEqualTo(expectedConstraints2);
        assertThat(constraintsAndExpr[1]).isEqualTo(expectedExpr);
        assertThat(atomicExpr.getRewrittenExpression()).isEqualTo(expectedExpr);

        expr = "field1#Class.field2!.field3";
        expectedConstraints1 = "field1 instanceof Class";
        expectedConstraints2 = "((Class)field1).field2 != null";
        expectedExpr = "((Class)field1).field2.field3";
        atomicExpr = new AtomicExprDescr(expr);
        ccd = new ConstraintConnectiveDescr();
        constraintsAndExpr = dumper.processImplicitConstraints(expr, atomicExpr, ccd, ccd.getDescrs().indexOf(atomicExpr), null);
        assertThat(ccd.getDescrs().get(0).toString()).isEqualTo(expectedConstraints1);
        assertThat(ccd.getDescrs().get(1).toString()).isEqualTo(expectedConstraints2);
        assertThat(constraintsAndExpr[1]).isEqualTo(expectedExpr);
        assertThat(atomicExpr.getRewrittenExpression()).isEqualTo(expectedExpr);
    }

    public ConstraintConnectiveDescr parse(final String constraint) {
        DrlExprParser parser = ParserTestUtils.getExprParser();
        ConstraintConnectiveDescr result = parser.parse(constraint);
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        return result;
    }
}
