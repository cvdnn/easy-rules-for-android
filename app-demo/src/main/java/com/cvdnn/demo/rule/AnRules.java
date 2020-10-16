package com.cvdnn.demo.rule;

import com.cvdnn.demo.App;
import com.cvdnn.demo.R;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.Log;
import org.jeasy.rules.core.RuleBuilder;
import org.jeasy.rules.mvel.MVELRule;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;

import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AnRules {

    public static void rainFire(boolean isRain) {
        Facts facts = new Facts();
        facts.put("rain", isRain);

        // define rules
        Rule weatherRule = loadMVELRule();
        Rules rules = new Rules();
        rules.register(weatherRule);

        // fire rules on known facts
        RulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, facts);
    }

    public static Rule buildWeatherRule() {
        return new RuleBuilder()
                .name(":: RuleBuilder")
                .description("if it rains then take an umbrella")
                .when(facts -> facts.get("rain").equals(true))
                .then(facts -> System.out.println("It rains, take an umbrella!"))
                .build();
    }

    public static Rule buildMVELRule() {
        return new MVELRule()
                .name(":: MVELRule")
                .description("if it rains then take an umbrella")
                .when("rain == true")
                .then("System.out.println(\"It rains, take an umbrella!\");");
    }

    public static Rule loadMVELRule() {
        Rule rule = null;

        try {
            rule = new MVELRuleFactory(new YamlRuleDefinitionReader()).createRule(
                    new InputStreamReader(App.Res.openRawResource(R.raw.weather_rule), UTF_8));
        } catch (Exception e) {
            Log.e(e);
        }

        return rule;
    }

    @org.jeasy.rules.annotation.Rule(name = ":: WeatherRule", description = "if it rains then take an umbrella")
    public static class WeatherRule {
        private static final String TAG = "WeatherRule";

        @Condition
        public boolean itRains(@Fact("rain") boolean rain) {
            return rain;
        }

        @Action
        public void takeAnUmbrella() {
            Log.i(TAG, "It rains, take an umbrella!");
        }
    }
}
