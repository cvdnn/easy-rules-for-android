package com.cvdnn.demo.yaml;

import com.cvdnn.demo.App;
import com.cvdnn.demo.R;

import org.yaml.snakeyaml.Yaml;

public class CodeYaml {

    public static CodeEntry parse() {
        CodeEntry entry = null;

        Yaml yaml = new Yaml();
        Object obj = yaml.loadAs(App.Res.openRawResource(R.raw.test), CodeEntry.class);
        if (obj instanceof CodeEntry) {
            entry = (CodeEntry) obj;
        }

        return entry;
    }
}
