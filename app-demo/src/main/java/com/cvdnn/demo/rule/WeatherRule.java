package com.cvdnn.demo.rule;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.core.Log;

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
