package com.skillbox.searchengine.connectionProperties;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Properties {
    private static Map<String, Object> map;

    static {
        Yaml yaml = new Yaml();
        InputStream inputStream = Properties.class.getClassLoader().getResourceAsStream("application.yml");
        map = yaml.load(inputStream);
    }

    private Properties() {}

    public static Map<String, Object> getMap() {
        return map;
    }

    /**
     * Extracts the list of sites that are needed to be indexed from the application.yml file and returns it as a map,
     * where key is url, and value is site name.
     * @return Map<String, String>, where key - url, value - name.
     */
    public static Map<String, String> getSites() {
        Map<String, String> result = new HashMap<>();
        List<Map<String, String>> list =  (List<Map<String, String>>) map.get("sites");
        list.forEach(map -> {
            result.put(map.get("url"), map.get("name"));
        });
        return result;
    }

    private static Map<String, String> getCredentials() {
        return (Map<String, String>) map.get("credentials");
    }

    public static String getUserAgent() {
        return getCredentials().get("user-agent");
    }

    public static String getReferrer() {
        return getCredentials().get("referrer");
    }

    public static String getWebInterface() {
        return (String) map.get("web-interface");
    }
}
