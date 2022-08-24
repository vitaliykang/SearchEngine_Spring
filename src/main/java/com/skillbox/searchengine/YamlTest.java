package com.skillbox.searchengine;

import com.skillbox.searchengine.connectionProperties.Properties;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlTest {

    public static void main(String[] args) throws Exception{
        System.out.println(Properties.getReferrer());
        System.out.println(Properties.getUserAgent());
        System.out.println(Properties.getWebInterface());

        Properties.getSites().forEach((url, name) -> {
            System.out.printf("%s - %s%n", name, url);
        });
    }
}
