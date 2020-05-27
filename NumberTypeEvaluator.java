package com.me;

import com.me.constants.StringConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NumberTypeEvaluator implements Evaluator {

    public static int minVal = 0;
    public static int maxVal = Integer.MAX_VALUE;
    public static HashMap<String, String> operatorMap;

    @Override
    public Map<String, rules> process(Map<String, String> paramConditionOldMap, Map<String, String> paramConditionNewMap) {
        Map<String, rules> result = new HashMap<>();
        for (String param : paramConditionNewMap.keySet()) {
            String[] existingRule = (null == paramConditionOldMap.getOrDefault(param, null)) ? null : new String[]{paramConditionOldMap.get(param)};
            result.put(param, isIntegerRuleEvaluation(existingRule, paramConditionNewMap.get(param)));
        }
        return result;
    }

    public static class Pair {
        int min;
        int max;

        public Pair() {
        }

        public Pair(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }

    static {
        operatorMap = new HashMap<>();
        operatorMap.put("GT", ">");
        operatorMap.put("GTE", ">=");
        operatorMap.put("LT", "<");
        operatorMap.put("LTE", "<=");
        operatorMap.put("EQ", "==");
    }

    private rules isIntegerRuleEvaluation(String[] existingRules, String newRule) {
        if (null == existingRules) {
            return checkValidRule(newRule);
        }

        rules valid = checkValidRule(newRule);
        if (valid == rules.INVALID) {
            // Error will be thrown to the UI
            System.out.println("Rule to be created is Invalid :" + newRule);
            return rules.INVALID;
        }

        List<Pair> checkMutualPairs = new ArrayList<>();
        for (String rule : existingRules) {
            Pair oldRuleRange = findIntersection(prpeareRuleList(rule));
            System.out.println("Old Range :" + " Min :" + oldRuleRange.min + " Max: " + oldRuleRange.max);
            Pair newRuleRange = findIntersection(prpeareRuleList(newRule));
            System.out.println("New Range :" + " Min :" + newRuleRange.min + " Max: " + newRuleRange.max);

            checkMutualPairs.add(oldRuleRange);
            checkMutualPairs.add(newRuleRange);
            return checkIfMutuallyExclusive(checkMutualPairs);
        }

        return rules.MATCHED;

    }

    private rules checkValidRule(String newRule) {
        List<Pair> pairs = prpeareRuleList(newRule);
        Pair finalRange = findIntersection(pairs);
        if (null != finalRange) {
            System.out.println("Final Range : [" + finalRange.min + "," + finalRange.max + "]");
        }
        return null == finalRange ? rules.INVALID : rules.VALID;
    }

    private rules checkIfMutuallyExclusive(List<Pair> pairs) {
        Pair finalRange = findIntersection(pairs);
        return null == finalRange ? rules.NOT_MATCHED : rules.MATCHED;
    }

    private Pair computeDeterministicSet(String operand, int operatorValue) {
        Pair pair = new Pair();
        switch (operand) {
            case "GT":
                pair.setMax(maxVal);
                pair.setMin(operatorValue + 1);
                break;

            case "GTE":
                pair.setMax(maxVal);
                pair.setMin(operatorValue);
                break;

            case "LT":
                pair.setMax(operatorValue - 1);
                pair.setMin(minVal);
                break;

            case "LTE":
                pair.setMax(operatorValue);
                pair.setMin(minVal);
                break;


            case "EQ":
                pair.setMax(operatorValue);
                pair.setMin(operatorValue);
                break;
        }
        return pair;
    }

    // Intersection has to be among all the intervals
    Pair findIntersection(List<Pair> pairs) {
        int lowerBound = -1;
        int upperBound = -1;

        for (Pair pair : pairs) {
            if (lowerBound == -1 || upperBound == -1) {
                lowerBound = pair.getMin();
                upperBound = pair.getMax();
            } else {
                if (pair.getMin() > upperBound || pair.getMax() < lowerBound) {
                    // Mutually Exclusive ranges, no intersection.
                    return null;
                } else {
                    lowerBound = Math.max(lowerBound, pair.getMin());
                    upperBound = Math.min(upperBound, pair.getMax());
                }
            }
        }
        return new Pair(lowerBound, upperBound);
    }

    List<Pair> prpeareRuleList(String newRule) {
        String[] singleRules = newRule.split(StringConstants.AND_KEYWORD);
        List<Pair> pairs = new ArrayList<>(singleRules.length);
        for (String singleRule : singleRules) {
            String rule = singleRule.trim();
            String[] token = rule.split(StringConstants.SPACE_KEYWORD);
            String operand = token[0];
            String operatorValue = token[1];
            pairs.add(computeDeterministicSet(operand, Integer.valueOf(operatorValue)));
        }
        return pairs;
    }

}
