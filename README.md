# EasyRules4Android

[EasyRules4Android](https://github.com/cvdnn/easy-rules-for-android.git) : A port of [EasyRules](https://github.com/j-easy/easy-rules.git) to Android by [@cvdnn](https://github.com/cvdnn)

Please read the EasyRules link for a quick intro to the concepts and workflows.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.jeasy/easy-rules-core/badge.svg?style=flat)](http://search.maven.org/#artifactdetails|org.jeasy|easy-rules-core|4.0.0|)

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

#### Or using an Expression Language:

```java
Rule weatherRule = new MVELRule()
        .name("weather rule")
        .description("if it rains then take an umbrella")
        .when("rain == true")
        .then("System.out.println(\"It rains, take an umbrella!\");");
```

#### Or using a MVEL rule descriptor:

Like in the following `mvel_rule.yml` example file:

```yaml
name: "weather rule"
description: "if it rains then take an umbrella"
condition: "rain == true"
actions:
  - "System.out.println(\"It rains, take an umbrella!\");"
```

```java
MVELRuleFactory ruleFactory = new MVELRuleFactory(new YamlRuleDefinitionReader());
Rule weatherRule = ruleFactory.createRule(App.Res.openRawResource(R.raw.wvel_rule), UTF_8));
```

#### Or using a Spel rule descriptor:

Like in the following `spel_rule.yml` example file:

```yaml
name: "weather rule"
description: "when it rains, then take an umbrella"
condition: "#{ ['rain'] == true }"
actions:
  - "#{ T(System).out.println(\"It rains, take an umbrella!\") }"
```

```java
SpELRuleFactory ruleFactory = new SpELRuleFactory(new YamlRuleDefinitionReader());
Rule weatherRule = ruleFactory.createRule(App.Res.openRawResource(R.raw.spel_rule), UTF_8));
```

### 2. Then, fire it!

```java
public static void rainFire(boolean isRain) {
    Facts facts = new Facts();
    facts.put("rain", isRain);

    // define rules
    Rule weatherRule = ...;
    Rules rules = new Rules();
    rules.register(weatherRule);

    // fire rules on known facts
    RulesEngine rulesEngine = new DefaultRulesEngine();
    rulesEngine.fire(rules, facts);
}
```