/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.1. You may not use this file
 * except in compliance with the Zeebe Community License 1.1.
 */
package io.camunda.zeebe.el;

import static io.camunda.zeebe.test.util.MsgPackUtil.asMsgPack;
import static org.assertj.core.api.Assertions.assertThat;

import io.camunda.zeebe.el.impl.StaticExpression;
import io.camunda.zeebe.el.util.TestFeelEngineClock;
import java.util.Map;
import org.junit.Test;

public class ExpressionLanguageTest {

  private static final EvaluationContext EMPTY_CONTEXT = name -> null;

  private final ExpressionLanguage expressionLanguage =
      ExpressionLanguageFactory.createExpressionLanguage(new TestFeelEngineClock());

  @Test
  public void shouldParseStaticStringValue() {
    final var expression = expressionLanguage.parseExpression("x");

    assertThat(expression).isNotNull();
    assertThat(expression.isStatic()).isTrue();
    assertThat(expression.isValid()).isTrue();
    assertThat(expression.getExpression()).isEqualTo("x");
    assertThat(expression.getVariableName()).isEmpty();
    assertThat(expression.getFailureMessage()).isNull();
  }

  @Test
  public void shouldParseStaticIntegerNumberValue() {
    // when
    final var expression = expressionLanguage.parseExpression("3");

    // then
    assertThat(expression.isStatic()).isTrue();
    assertThat(expression.isValid()).isTrue();
    assertThat(expression.getExpression()).isEqualTo("3");
    assertThat(expression.getVariableName()).isEmpty();
    assertThat(expression.getFailureMessage()).isNull();
  }

  @Test
  public void shouldParseStaticDoubleNumberValue() {
    // when
    final var expression = expressionLanguage.parseExpression("3.141");

    // then
    assertThat(expression.isStatic()).isTrue();
    assertThat(expression.isValid()).isTrue();
    assertThat(expression.getExpression()).isEqualTo("3.141");
    assertThat(expression.getVariableName()).isEmpty();
    assertThat(expression.getFailureMessage()).isNull();
  }

  @Test
  public void shouldParseExpression() {
    final var expression = expressionLanguage.parseExpression("=x.y");

    assertThat(expression).isNotNull();
    assertThat(expression.isStatic()).isFalse();
    assertThat(expression.isValid()).isTrue();
    assertThat(expression.getExpression()).isEqualTo("x.y");
    assertThat(expression.getVariableName()).contains("x");
    assertThat(expression.getFailureMessage()).isNull();
  }

  @Test
  public void shouldParseMultilineExpression() {
    final var expression = expressionLanguage.parseExpression("={\nx:1\n}");

    assertThat(expression).isNotNull();
    assertThat(expression.isStatic()).isFalse();
    assertThat(expression.isValid()).isTrue();
    assertThat(expression.getExpression()).isEqualTo("{\nx:1\n}");
    assertThat(expression.getVariableName()).isEmpty();
    assertThat(expression.getFailureMessage()).isNull();
  }

  @Test
  public void shouldParseInvalidExpression() {
    final var expression = expressionLanguage.parseExpression("=x ?! 5");

    assertThat(expression).isNotNull();
    assertThat(expression.isValid()).isFalse();
    assertThat(expression.getExpression()).isEqualTo("x ?! 5");
    assertThat(expression.getVariableName()).isEmpty();
    assertThat(expression.getFailureMessage()).startsWith("failed to parse expression 'x ?! 5'");
  }

  @Test
  public void shouldEvaluateStaticStringValue() {
    final var expression = expressionLanguage.parseExpression("x");
    final var evaluationResult = expressionLanguage.evaluateExpression(expression, EMPTY_CONTEXT);

    assertThat(evaluationResult).isNotNull();
    assertThat(evaluationResult.isFailure()).isFalse();
    assertThat(evaluationResult.getType()).isEqualTo(ResultType.STRING);
    assertThat(evaluationResult.getString()).isEqualTo("x");
    assertThat(evaluationResult.getExpression()).isEqualTo("x");
    assertThat(expression.getVariableName()).isEmpty();
    assertThat(evaluationResult.getFailureMessage()).isNull();
  }

  @Test
  public void shouldEvaluateStaticIntegerNumberValue() {
    final var expression = expressionLanguage.parseExpression("3");
    final var evaluationResult = expressionLanguage.evaluateExpression(expression, EMPTY_CONTEXT);

    assertThat(evaluationResult).isNotNull();
    assertThat(evaluationResult.isFailure()).isFalse();
    assertThat(evaluationResult.getType()).isEqualTo(ResultType.NUMBER);
    assertThat(evaluationResult.getNumber().longValue()).isEqualTo(3);
    assertThat(evaluationResult.getExpression()).isEqualTo("3");
    assertThat(expression.getVariableName()).isEmpty();
    assertThat(evaluationResult.getFailureMessage()).isNull();
  }

  @Test
  public void shouldParseDoubleNumbers() {
    final var expression = expressionLanguage.parseExpression("3.141");
    final var evaluationResult = expressionLanguage.evaluateExpression(expression, EMPTY_CONTEXT);

    assertThat(evaluationResult).isNotNull();
    assertThat(evaluationResult.isFailure()).isFalse();
    assertThat(evaluationResult.getType()).isEqualTo(ResultType.NUMBER);
    assertThat(evaluationResult.getNumber().doubleValue()).isEqualTo(3.141);
    assertThat(evaluationResult.getExpression()).isEqualTo("3.141");
    assertThat(expression.getVariableName()).isEmpty();
    assertThat(evaluationResult.getFailureMessage()).isNull();
    // given
    final StaticExpression sutStaticExpression = new StaticExpression("3.141");
  }

  @Test
  public void shouldParseStrings() {
    // given
    final StaticExpression sutStaticExpression = new StaticExpression("lorem ipsum");

    // then
    assertThat(sutStaticExpression.isValid()).isTrue();
    assertThat(sutStaticExpression.getType()).isEqualTo(ResultType.STRING);
    assertThat(sutStaticExpression.getString()).isEqualTo("lorem ipsum");

    assertThat(sutStaticExpression.getBoolean()).isNull();
    assertThat(sutStaticExpression.getNumber()).isNull();
  }

  @Test
  public void shouldEvaluateExpression() {
    final var expression = expressionLanguage.parseExpression("=x");
    final var evaluationResult =
        expressionLanguage.evaluateExpression(expression, Map.of("x", asMsgPack("\"x\""))::get);

    assertThat(evaluationResult).isNotNull();
    assertThat(evaluationResult.isFailure()).isFalse();
    assertThat(evaluationResult.getType()).isEqualTo(ResultType.STRING);
    assertThat(evaluationResult.getString()).isEqualTo("x");
    assertThat(evaluationResult.getExpression()).isEqualTo("x");
    assertThat(expression.getVariableName()).contains("x");
    assertThat(evaluationResult.getFailureMessage()).isNull();
  }

  @Test
  public void shouldEvaluateExpressionWithMissingVariables() {
    final var expression = expressionLanguage.parseExpression("=x");
    final var evaluationResult = expressionLanguage.evaluateExpression(expression, EMPTY_CONTEXT);

    assertThat(evaluationResult).isNotNull();
    assertThat(evaluationResult.isFailure()).isFalse();
    assertThat(evaluationResult.getType()).isEqualTo(ResultType.NULL);
    assertThat(evaluationResult.getExpression()).isEqualTo("x");
    assertThat(expression.getVariableName()).contains("x");
    assertThat(evaluationResult.getFailureMessage()).isNull();
  }
}
