package com.skillbox.searchengine.utils;

import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    public static void log(String msg) {
        try (FileWriter writer = new FileWriter("src/main/resources/output.txt", true)) {
            writer.write(msg);
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
