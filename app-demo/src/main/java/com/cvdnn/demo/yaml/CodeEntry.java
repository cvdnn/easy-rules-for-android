package com.cvdnn.demo.yaml;

import java.util.List;
import java.util.Map;

public class CodeEntry {
    private List<String> languages;
    private Map<String, String> websites;

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public void setWebsites(Map<String, String> websites) {
        this.websites = websites;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public Map<String, String> getWebsites() {
        return websites;
    }
}
