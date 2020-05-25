package com.me.entity;

import java.util.List;

public class RuleLine {

    private List<Rule> rules;
    private List<String> mappedActionKeys;

    public RuleLine() {
    }

    public RuleLine(List<Rule> rules, List<String> mappedActionKeys) {
        this.rules = rules;
        this.mappedActionKeys = mappedActionKeys;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public List<String> getMappedActionKeys() {
        return mappedActionKeys;
    }

    public void setMappedActionKeys(List<String> mappedActionKeys) {
        this.mappedActionKeys = mappedActionKeys;
    }
}
