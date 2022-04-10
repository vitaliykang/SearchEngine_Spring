package com.skillbox.searchengine;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.*;

public class TextAnalyzer {
    private static LuceneMorphology morphology;

    static {
        try {
            morphology = new RussianLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TextAnalyzer(){};

    /**
     * Counts the number of unique lemmas in the text and returns the result as a hash map
     * @param text in Russian language
     * @return map of lexemes in the text
     */
    public static Map<String, Integer> parseText(String text) {
        Map<String, Integer> result = new HashMap<>();
        String[] textArray = text.split(" ");

        for (int i = 0; i < textArray.length; i++) {
            List<String> morphInfo = getMorphInfo(textArray[i]);
            for (String info : morphInfo) {
                if (isWord(info)) {
                    int index = info.indexOf('|');
                    String word = index == -1 ? info : info.substring(0, index);
                    Integer count = result.putIfAbsent(word, 1);
                    if (count != null) {
                        result.replace(word, count + 1);
                    }
                }
            }
        }

        return result;
    }

    /**
     *
     * @param morphInfo from LuceneMorphology's getMorphInfo method
     * @return boolean
     */
    private static Boolean isWord(String morphInfo) {
        if (morphInfo.endsWith("ПРЕДЛ") ||
                morphInfo.contains("МЕЖД") ||
                morphInfo.contains("СОЮЗ") ||
                morphInfo.contains("ПРЕДК") ||
                morphInfo.contains("МС") ||
                morphInfo.contains("МС-П")) {
            return false;
        }
        return true;
    }

    /**
     * @param word
     * @return list of strings containing morph info on the provided word
     */
    private static List<String> getMorphInfo(String word) {
        List<String> result = new ArrayList<>();
        word = word.toLowerCase(Locale.ROOT);
        String originalWord = word;

        try {
            result = morphology.getMorphInfo(word);
        } catch (WrongCharaterException e) {
            //if exception is thrown, will try to remove the last char in case if it is a punctuation character
            word = word.substring(0, word.length() - 1);

            try {
                result = morphology.getMorphInfo(word);
            } catch (WrongCharaterException e2) {
                //in case of the second exception will return a list containing the original word
                result.add(originalWord);
            }
        }
        return result;
    }
}
