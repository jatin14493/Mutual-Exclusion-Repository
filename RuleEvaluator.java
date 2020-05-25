package com.me;

import com.me.constants.StringConstants;
import com.me.entity.Rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RuleEvaluator {

    private Evaluator numberTypeEvaluator = new NumberTypeEvaluator();
    private Evaluator stringTypeEvaluator = new StringTypeEvaluator();

    public String process(List<Rule> rulesOld, List<Rule> rulesNew) throws Exception {


        // true -> String
        // false -> Non-String
        Map<Boolean, Map<String, String>> paramConditionsOld = rulesOld.stream().collect(Collectors.partitioningBy(rule -> rule.getConditionalExpression().contains(StringConstants.CONTAINS), Collectors.toMap(Rule::getParameterName, Rule::getConditionalExpression)));
        Map<Boolean, Map<String, String>> paramConditionsNew = rulesNew.stream().collect(Collectors.partitioningBy(rule -> rule.getConditionalExpression().contains(StringConstants.CONTAINS), Collectors.toMap(Rule::getParameterName, Rule::getConditionalExpression)));

        Map<String, rules> resultString = new HashMap<>();
        Map<String, rules> resultNonString = new HashMap<>();

        for (Map.Entry<Boolean, Map<String, String>> params : paramConditionsNew.entrySet()) {
            if (Boolean.FALSE.equals(params.getKey()))
                resultNonString = numberTypeEvaluator.process(paramConditionsOld.get(Boolean.FALSE), paramConditionsNew.get(Boolean.FALSE));
            else if (Boolean.TRUE.equals(params.getKey()))
                resultString = stringTypeEvaluator.process(paramConditionsOld.get(Boolean.TRUE), paramConditionsNew.get(Boolean.TRUE));
        }

        boolean areStringRulesUnique = resultString.values().stream().allMatch(rules -> rules.equals(com.me.rules.NOT_MATCHED));
        boolean areNonStringRulesUnique = resultNonString.values().stream().allMatch(rules -> rules.equals(com.me.rules.NOT_MATCHED));
        return areStringRulesUnique && areNonStringRulesUnique ? "UNIQUE" : "NON_UNIQUE";
    }

}
