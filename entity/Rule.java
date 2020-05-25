package com.me.entity;

import java.util.Objects;

public class Rule {

    String ruleId;
    String parameterName;
    String conditionalExpression;

    public Rule() {
    }

    public Rule(String ruleId, String parameterName, String conditionalExpression) {
        this.ruleId = ruleId;
        this.parameterName = parameterName;
        this.conditionalExpression = conditionalExpression;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getConditionalExpression() {
        return conditionalExpression;
    }

    public void setConditionalExpression(String conditionalExpression) {
        this.conditionalExpression = conditionalExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(ruleId, rule.ruleId) &&
                Objects.equals(parameterName, rule.parameterName) &&
                Objects.equals(conditionalExpression, rule.conditionalExpression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, parameterName, conditionalExpression);
    }
}
