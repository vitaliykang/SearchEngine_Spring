package com.skillbox.searchengine.utils;

import com.skillbox.searchengine.entity.Field;
import com.skillbox.searchengine.entity.Lemma;
import com.skillbox.searchengine.repository.CustomRepository;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class PageProcessor {
    private static LuceneMorphology morphology;

    static {
        try {
            morphology = new RussianLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PageProcessor(){};


    /**
     * Generates a hash map containing all lemmas and their respective rank
     * @param document Jsoup document
     * @return Map<Lemma, Double>
     */
    public static Map<Lemma, Double> generateMap(Document document) {
        Map<Lemma, Double> result = new HashMap<>();
        List<Field> fields = CustomRepository.findAll(Field.class);

        fields.forEach(field -> {
            Elements elements = document.select(field.getSelector());
            Map<Lemma, Integer> lemmaCount = countLemmas(elements.text());

            lemmaCount.forEach((lemma, count) -> {
                double rank = count * field.getWeight();
                Double oldRank = result.putIfAbsent(lemma, rank);
                if (oldRank != null) {
                    result.replace(lemma, rank + oldRank);
                }
            });
        });

        return result;
    }

    /**
     * Counts the number of unique lemmas in the text and returns the result as a hash map
     * @param text in Russian language
     * @return map of lexemes in the text
     */
    private static Map<Lemma, Integer> countLemmas(String text) {
        Map<Lemma, Integer> result = new HashMap<>();
        String[] textArray = text.split(" ");

        for (int i = 0; i < textArray.length; i++) {
            //making sure that the word length is greater than 1 char
            if (textArray[i].length() <= 1) {
                continue;
            }

            List<String> morphInfo = getMorphInfo(textArray[i]);
            for (String info : morphInfo) {
                if (isWord(info)) {
                    int index = info.indexOf('|');
                    String word = index == -1 ? info : info.substring(0, index);
                    Lemma lemma = new Lemma();
                    lemma.setLemma(word);
                    lemma.setFrequency(1);
                    Integer count = result.putIfAbsent(lemma, 1);
                    if (count != null) {
                        result.replace(lemma, count + 1);
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
