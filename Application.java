package com.me;

import com.me.entity.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Application {

    public static void main(String[] args) {
        RuleEvaluator ruleEvaluator = new RuleEvaluator();
        List<List<Rule>> oldRules = new ArrayList<>();

        Rule ruleOld1 = new Rule("1", "Amount", "GT 100 && LTE 200");
        Rule ruleOld2 = new Rule("2", "Customer", "CONTAINS 1,2");
        Rule ruleOld3 = new Rule("3", "Department", "CONTAINS 1");
        List<Rule> rulesOld = new ArrayList<>();
        rulesOld.add(ruleOld1);
        rulesOld.add(ruleOld2);
        rulesOld.add(ruleOld3);

        Rule ruleOld11 = new Rule("1", "Amount", "GT 200 && LTE 1000");
        Rule ruleOld12 = new Rule("2", "Customer", "CONTAINS 3,4");
        Rule ruleOld13 = new Rule("3", "Department", "CONTAINS 2");
        List<Rule> rulesOld1 = new ArrayList<>();
        rulesOld1.add(ruleOld11);
        rulesOld1.add(ruleOld12);
        rulesOld1.add(ruleOld13);

        oldRules.add(rulesOld);
        oldRules.add(rulesOld1);

        Rule ruleNew1 = new Rule("1", "Amount", "GT 1000");
        Rule ruleNew2 = new Rule("2", "Customer", "NOT_CONTAINS 1,2,3,4");
        Rule ruleNew3 = new Rule("3", "Department", "CONTAINS 3,4");
        List<Rule> rulesNew = new ArrayList<>();
        rulesNew.add(ruleNew1);
        rulesNew.add(ruleNew2);
        rulesNew.add(ruleNew3);

        Set<String> outputs = oldRules.parallelStream().map(t -> {
            try {
                return ruleEvaluator.process(t, rulesNew);
            } catch (Exception e) {
            }
            return "Invalid Rules";
        }).collect(Collectors.toSet());

        System.out.println("Rules are:" + outputs);
    }
}
