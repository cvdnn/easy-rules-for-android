package com.cvdnn.demo.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.cvdnn.demo.App;
import com.cvdnn.demo.R;
import com.cvdnn.demo.rule.AnRules;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.Task.execute(() -> AnRules.rainFire(true));
    }
}