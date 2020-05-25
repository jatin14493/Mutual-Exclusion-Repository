package com.me;

import com.me.constants.StringConstants;

import java.util.*;
import java.util.stream.Collectors;

public class StringTypeEvaluator implements Evaluator {

    public static int minVal = 0;
    public static int maxVal = Integer.MAX_VALUE;

    @Override
    public Map<String, rules> process(Map<String, String> paramConditionOldMap, Map<String, String> paramConditionNewMap) throws Exception {
        Map<String, rules> result = new HashMap<>();
        for (String param : paramConditionNewMap.keySet()) {
            String[] existingRule = (null == paramConditionOldMap.getOrDefault(param, null)) ? null : new String[]{paramConditionOldMap.get(param)};
            result.put(param, isStringRuleEvaluation(existingRule, paramConditionNewMap.get(param)));
        }
        return result;
    }

    /***
     * Enum Usage
     *
     * MATCHED : Rule Matches, Hence can't be created.
     * NOT_MATCHED : Rule is unique
     * INVALID : Rule Created is Invalid
     * VALID : Rule Created is Valid
     */



    /*public static void main(String[] args) {
        // write your code here
        String[] existingRules = new String[]{"CONTAINS 1,2,3"};
        String newRule = "CONTAINS 1,2";
        // Matched [If not present, that means for any customer which is anyMatch() use case]
        System.out.println("1." + isStringRuleEvaluation(null, newRule));
        assert isStringRuleEvaluation(existingRules, newRule).equals(rules.MATCHED) : true;


        // Matched
        System.out.println("2." + isStringRuleEvaluation(existingRules, newRule));
        assert isStringRuleEvaluation(existingRules, newRule).equals(rules.MATCHED) : true;


        //Unmatched
        existingRules = new String[]{"CONTAINS 1,2,3,4"};
        newRule = "CONTAINS 5,6";
        System.out.println("3." + isStringRuleEvaluation(existingRules, newRule));
        assert isStringRuleEvaluation(existingRules, newRule).equals(rules.NOT_MATCHED) : true;


        //Matched
        existingRules = new String[]{"CONTAINS 1,2,3,4"};
        newRule = "CONTAINS 3,4,5,6";
        System.out.println("4." + isStringRuleEvaluation(existingRules, newRule));
        assert (isStringRuleEvaluation(existingRules, newRule).equals(rules.MATCHED));

        // Unmatched
        existingRules = new String[]{"NOT_CONTAINS 1,2,3,4"};
        newRule = "CONTAINS 1,2,3,4";
        System.out.println("5." + isStringRuleEvaluation(existingRules, newRule));
        assert isStringRuleEvaluation(existingRules, newRule).equals(rules.NOT_MATCHED) : true;

        // Matched
        existingRules = new String[]{"CONTAINS 1,2,3,4"};
        newRule = "NOT_CONTAINS 5,6";
        System.out.println("6." + isStringRuleEvaluation(existingRules, newRule));
        assert isStringRuleEvaluation(existingRules, newRule).equals(rules.MATCHED) : true;

        // Matched
        existingRules = new String[]{"NOT_CONTAINS 1,2,3,4"};
        newRule = "NOT_CONTAINS 5,6";
        System.out.println("7." + isStringRuleEvaluation(existingRules, newRule));
        assert isStringRuleEvaluation(existingRules, newRule).equals(rules.MATCHED) : true;

        // Matched
        existingRules = new String[]{"NOT_CONTAINS 1,2,3,4"};
        newRule = "NOT_CONTAINS 1,2,3,4";
        System.out.println("8." + isStringRuleEvaluation(existingRules, newRule));
        assert isStringRuleEvaluation(existingRules, newRule).equals(rules.MATCHED) : true;

        // Matched
        existingRules = new String[]{"NOT_CONTAINS 1,2,3,4"};
        newRule = "NOT_CONTAINS 1,2,3";
        System.out.println("9." + isStringRuleEvaluation(existingRules, newRule));
        assert isStringRuleEvaluation(existingRules, newRule).equals(rules.MATCHED) : true;
    }*/
    private rules isStringRuleEvaluation(String[] existingRules, String newRule) throws Exception {
        if (null == existingRules) {
            // String we are supporting contains and Not Contains [Either equal or not equal]
            return rules.MATCHED;
        }

        Map<String, List<String>> newRuleMap = prepareRuleMap(newRule);
        for (String rule : existingRules) {
            Map<String, List<String>> oldRuleMap = prepareRuleMap(rule);
            checkValidRule(newRuleMap);
            return checkForMutualExclusion(oldRuleMap, newRuleMap);
        }

        return rules.MATCHED;

    }

    private void checkValidRule(Map<String, List<String>> newRuleMap) throws Exception {
        boolean useCaseNewRule = newRuleMap.keySet().contains(StringConstants.NOT_CONTAINS); if(useCaseNewRule){
            rules val = isValidRule(newRuleMap.get(StringConstants.NOT_CONTAINS));
            if (val.equals(rules.INVALID))
                throw new Exception("Invalid Rule : Not Contains Not Applicable on Entire Range");
        }

    }

    private rules checkForMutualExclusion(Map<String, List<String>> oldRuleMap, Map<String, List<String>> newRuleMap) throws Exception {
        /***
         * false  = CONTAINS use case and true = NOT_CONTAINS use case
         */


        boolean useCaseOleRule = oldRuleMap.keySet().contains(StringConstants.NOT_CONTAINS);
        boolean useCaseNewRule = newRuleMap.keySet().contains(StringConstants.NOT_CONTAINS);

        //Base Condition [Not Contains All Department/Customer is Invalid Rule]
        if(useCaseNewRule){
            rules val = isValidRule(newRuleMap.get(StringConstants.NOT_CONTAINS));
            if (val.equals(rules.INVALID))
                throw new Exception("Invalid Rule : Not Contains Not Applicable on Entire Range");
        }

        //both contains use case
        if (!useCaseNewRule && !useCaseOleRule) {
            HashSet<String> newValues = new HashSet<>(newRuleMap.get(StringConstants.CONTAINS));
            HashSet<String> oldValues = new HashSet<>(oldRuleMap.get(StringConstants.CONTAINS));
            newValues.retainAll(oldValues);
            return newValues.isEmpty() ? rules.NOT_MATCHED : rules.MATCHED;
        } else if (useCaseNewRule && !useCaseOleRule) {
            HashSet<String> newValues = new HashSet<>(newRuleMap.get(StringConstants.NOT_CONTAINS));
            HashSet<String> oldValues = new HashSet<>(oldRuleMap.get(StringConstants.CONTAINS));
            newValues.retainAll(oldValues);
            return (newValues.equals(oldValues) ? rules.NOT_MATCHED : rules.MATCHED);
        } else if (!useCaseNewRule && useCaseOleRule) {
            HashSet<String> newValues = new HashSet<>(newRuleMap.get(StringConstants.CONTAINS));
            HashSet<String> oldValues = new HashSet<>(oldRuleMap.get(StringConstants.NOT_CONTAINS));
            newValues.retainAll(oldValues);
            return (newValues.equals(oldValues) ? rules.NOT_MATCHED : rules.MATCHED);
        } else if (useCaseNewRule && useCaseOleRule) {
            /**
             * For Both Not_Contains Use Case, the criteria will be unmatched, for example
             * Case 1
             * 1. NOT_CONTAINS 1,2
             * 2. NOT_CONTAINS 1,2 this case, they're matched
             *
             * Case 2
             * 1. NOT_CONTAINS 1,2,3
             * 2. NOT_CONTAINS 1,2 this case , they're matched since we're unaware of entire set of finite string values
             *
             * Case 3
             * 1. NOT_CONTAINS 1,2,3
             * 2. NOT_CONTAINS 4,5 this case , they're matched since we're unaware of entire set of finite string values.
             * Let's say Customer has values from 1,100 then rules in case 3 becomes mutually inclusive but if total values are [1,2,3,4,5] then they are mutually exclusive.
             *
             * Safer Assumption in this scenario would be to have it as Matched and let other factors decide.
             */
            return rules.MATCHED;
        }

        return rules.MATCHED;
    }


    Map<String, List<String>> prepareRuleMap(String rule) {
        String[] singleRules = rule.split(StringConstants.SPACE_KEYWORD);
        List<String> values = Arrays.asList(singleRules[1].split(StringConstants.COMMA_KEYWORD));  //Values like 1,2,3
        Map<String, List<String>> valueMap = new HashMap<>();
        valueMap.put(singleRules[0], values);
        return valueMap;
    }

    private rules isValidRule(List<String> rule){
        boolean isSelectAll = rule.stream().anyMatch(val -> val.contains("ALL"));
        return isSelectAll ? rules.INVALID : rules.VALID;
    }

}
