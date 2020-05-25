package com.me;

import java.util.Map;

public interface Evaluator {

    Map<String, rules> process(Map<String, String> paramConditionOldMap, Map<String, String> paramConditionNewMap) throws Exception;
}
