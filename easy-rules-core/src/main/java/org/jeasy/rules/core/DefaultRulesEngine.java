/*
 * The MIT License
 *
 *  Copyright (c) 2020, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.jeasy.rules.core;

import org.jeasy.rules.api.Fact;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.api.RulesEngineParameters;

import java.util.HashMap;
import java.util.Map;

/**
 * Default {@link RulesEngine} implementation.
 * <p>
 * Rules are fired according to their natural order which is priority by default.
 * This implementation iterates over the sorted set of rules, evaluates the condition
 * of each rule and executes its actions if the condition evaluates to true.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public final class DefaultRulesEngine extends AbstractRulesEngine {
    private static final String TAG = "DefaultRulesEngine";

    /**
     * Create a new {@link DefaultRulesEngine} with default parameters.
     */
    public DefaultRulesEngine() {
        super();
    }

    /**
     * Create a new {@link DefaultRulesEngine}.
     *
     * @param parameters of the engine
     */
    public DefaultRulesEngine(final RulesEngineParameters parameters) {
        super(parameters);
    }

    @Override
    public void fire(Rules rules, Facts facts) {
        triggerListenersBeforeRules(rules, facts);
        doFire(rules, facts);
        triggerListenersAfterRules(rules, facts);
    }

    void doFire(Rules rules, Facts facts) {
        if (rules.isEmpty()) {
            Log.w(TAG, "No rules registered! Nothing to apply");
            return;
        }
        logEngineParameters();
        log(rules);
        log(facts);
        Log.d(TAG, "Rules evaluation started");
        for (Rule rule : rules) {
            final String name = rule.getName();
            final int priority = rule.getPriority();
            if (priority > parameters.getPriorityThreshold()) {
                Log.d(TAG, "Rule priority threshold (%s) exceeded at rule '%s' with priority=%s, next rules will be skipped",
                        parameters.getPriorityThreshold(), name, priority);
                break;
            }
            if (!shouldBeEvaluated(rule, facts)) {
                Log.d(TAG, "Rule '%s' has been skipped before being evaluated", name);
                continue;
            }
            boolean evaluationResult = false;
            try {
                evaluationResult = rule.evaluate(facts);
            } catch (RuntimeException exception) {
                android.util.Log.e(TAG, "Rule '" + name + "' evaluated with error", exception);

                triggerListenersOnEvaluationError(rule, facts, exception);
                // give the option to either skip next rules on evaluation error or continue by considering the evaluation error as false
                if (parameters.isSkipOnFirstNonTriggeredRule()) {
                    Log.d(TAG, "Next rules will be skipped since parameter skipOnFirstNonTriggeredRule is set");
                    break;
                }
            }
            if (evaluationResult) {
                Log.d(TAG, "Rule '%s' triggered", name);
                triggerListenersAfterEvaluate(rule, facts, true);
                try {
                    triggerListenersBeforeExecute(rule, facts);
                    rule.execute(facts);
                    Log.d(TAG, "Rule '%s' performed successfully", name);

                    triggerListenersOnSuccess(rule, facts);
                    if (parameters.isSkipOnFirstAppliedRule()) {
                        Log.i(TAG, "Next rules will be skipped since parameter skipOnFirstAppliedRule is set");
                        break;
                    }
                } catch (Exception exception) {
                    android.util.Log.e(TAG, "Rule '" + name + "' performed with error", exception);

                    triggerListenersOnFailure(rule, exception, facts);
                    if (parameters.isSkipOnFirstFailedRule()) {
                        Log.d("Next rules will be skipped since parameter skipOnFirstFailedRule is set");
                        break;
                    }
                }
            } else {
                Log.d("Rule '%s' has been evaluated to false, it has not been executed", name);
                triggerListenersAfterEvaluate(rule, facts, false);
                if (parameters.isSkipOnFirstNonTriggeredRule()) {
                    Log.d(TAG, "Next rules will be skipped since parameter skipOnFirstNonTriggeredRule is set");
                    break;
                }
            }
        }
    }

    private void logEngineParameters() {
        Log.d("%s", parameters);
    }

    private void log(Rules rules) {
        Log.d(TAG, "Registered rules:");
        for (Rule rule : rules) {
            Log.d(TAG, "Rule { name = '%s', description = '%s', priority = '%s'}",
                    rule.getName(), rule.getDescription(), rule.getPriority());
        }
    }

    private void log(Facts facts) {
        Log.d(TAG, "Known facts:");
        for (Fact<?> fact : facts) {
            Log.d(TAG, "%s", fact);
        }
    }

    @Override
    public Map<Rule, Boolean> check(Rules rules, Facts facts) {
        triggerListenersBeforeRules(rules, facts);
        Map<Rule, Boolean> result = doCheck(rules, facts);
        triggerListenersAfterRules(rules, facts);
        return result;
    }

    private Map<Rule, Boolean> doCheck(Rules rules, Facts facts) {
        Log.d(TAG, "Checking rules");
        Map<Rule, Boolean> result = new HashMap<>();
        for (Rule rule : rules) {
            if (shouldBeEvaluated(rule, facts)) {
                result.put(rule, rule.evaluate(facts));
            }
        }
        return result;
    }

    private void triggerListenersOnFailure(final Rule rule, final Exception exception, Facts facts) {
        ruleListeners.forEach(ruleListener -> ruleListener.onFailure(rule, facts, exception));
    }

    private void triggerListenersOnSuccess(final Rule rule, Facts facts) {
        ruleListeners.forEach(ruleListener -> ruleListener.onSuccess(rule, facts));
    }

    private void triggerListenersBeforeExecute(final Rule rule, Facts facts) {
        ruleListeners.forEach(ruleListener -> ruleListener.beforeExecute(rule, facts));
    }

    private boolean triggerListenersBeforeEvaluate(Rule rule, Facts facts) {
        return ruleListeners.stream().allMatch(ruleListener -> ruleListener.beforeEvaluate(rule, facts));
    }

    private void triggerListenersAfterEvaluate(Rule rule, Facts facts, boolean evaluationResult) {
        ruleListeners.forEach(ruleListener -> ruleListener.afterEvaluate(rule, facts, evaluationResult));
    }

    private void triggerListenersOnEvaluationError(Rule rule, Facts facts, Exception exception) {
        ruleListeners.forEach(ruleListener -> ruleListener.onEvaluationError(rule, facts, exception));
    }

    private void triggerListenersBeforeRules(Rules rule, Facts facts) {
        rulesEngineListeners.forEach(rulesEngineListener -> rulesEngineListener.beforeEvaluate(rule, facts));
    }

    private void triggerListenersAfterRules(Rules rule, Facts facts) {
        rulesEngineListeners.forEach(rulesEngineListener -> rulesEngineListener.afterExecute(rule, facts));
    }

    private boolean shouldBeEvaluated(Rule rule, Facts facts) {
        return triggerListenersBeforeEvaluate(rule, facts);
    }

}
