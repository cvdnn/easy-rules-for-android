# EasyRules4Android

A port of EasyRules to Android
http://www.easyrules.org/

Please read the EasyRules link for a quick intro to the concepts and workflows.

[![Build Status](https://travis-ci.org/CrowdStrike/easyrulesgo.svg?branch=master)](https://travis-ci.org/CrowdStrike/easyrulesgo)

  - Implements default rules engine
  - Allows for composite rule (all or nothing)

## Based on [Easy Rules](https://github.com/j-easy/easy-rules/wiki) and the [Java/Maven Tutorials](https://github.com/j-easy/easy-rules/tree/master/easy-rules-tutorials)
### Usage:
### 1. First, define your rule..

#### Either in a declarative way using annotations:

```java
@Rule(name = "weather rule", description = "if it rains then take an umbrella")
public class WeatherRule {
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
```

#### Or in a programmatic way with a fluent API:

```java
Rule weatherRule = new RuleBuilder()
        .name("weather rule")
        .description("if it rains then take an umbrella")
        .when(facts -> facts.get("rain").equals(true))
        .then(facts -> System.out.println("It rains, take an umbrella!"))
        .build();
```

### 2. Then, fire it!

```java
    public static void rainFire(boolean isRain) {
        Facts facts = new Facts();
        facts.put("rain", isRain);

        // define rules
        WeatherRule weatherRule = new WeatherRule();
        Rules rules = new Rules();
        rules.register(weatherRule);

        // fire rules on known facts
        RulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, facts);
    }
```