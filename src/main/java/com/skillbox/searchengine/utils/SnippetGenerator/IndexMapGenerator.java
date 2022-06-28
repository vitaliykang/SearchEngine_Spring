package com.skillbox.searchengine.utils.SnippetGenerator;

import com.skillbox.searchengine.entity.Lemma;
import com.skillbox.searchengine.utils.LemmaCounter;
import org.jsoup.nodes.Document;

import java.util.*;
import java.util.concurrent.RecursiveTask;

/**
 * RecursiveTask type class, that generates a map of indexes pointing to the location of words from the search request
 * in the body of a web page. SnippetGenerator class dependency.
 */
public class IndexMapGenerator extends RecursiveTask<Map<Integer, Boolean>> {
    private List<Lemma> requestLemmas;
    private Document document;

    public IndexMapGenerator(List<Lemma> requestLemmas, Document document) {
        this.requestLemmas = requestLemmas;
        this.document = document;
    }

    @Override
    protected Map<Integer, Boolean> compute() {
        if (requestLemmas.size() > 1) {
            Map<Integer, Boolean> result = new HashMap<>();
            splitTask().forEach(subtask -> {
                Map<Integer, Boolean> subtaskMap = subtask.compute();
                subtaskMap.forEach((key, value) -> {
                   Boolean returnValue = result.put(key, value);
                   if (returnValue != null && returnValue) {
                       result.put(key, true);
                   }
                });
            });

            return result;
        } else {
            return process();
        }
    }

    /*
     * Generates a hash map of indexes of the searched word and words around it.
    */
    private Map<Integer, Boolean> process() {
        Map<Integer, Boolean> result = new HashMap<>();

        Lemma lemma = requestLemmas.get(0);
        String[] body = document.body().text().split(" ");
        String shortLemma = lemma.getLemma().substring(0, 4);

        //if current element contains part of the lemma, it gets morph info on the given element and extracts
        //the word part to compare it to the current element. If they are equal, adds its index and its surrounding
        // indexes to the result map.
        for(int i = 0; i < body.length; i++) {
            if (body[i].contains(shortLemma)) {
                List<String> morphInfo = LemmaCounter.getMorphInfo(body[i]);
                for (String info : morphInfo) {
                    String word = LemmaCounter.getWordFromMorphInfo(info);
                    if (word.equals(lemma.getLemma())) {
                        for (int j = i - 3; j < i + 3; j++) {
                            if (j == i) {
                                result.put(j, true);
                            } else {
                                result.put(j, false);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    private List<IndexMapGenerator> splitTask() {
        List<IndexMapGenerator> subtasks = new ArrayList<>();
        int half = requestLemmas.size() / 2;

        IndexMapGenerator generator1 = new IndexMapGenerator(requestLemmas.subList(0, half), document);
        IndexMapGenerator generator2 = new IndexMapGenerator(requestLemmas.subList(half, requestLemmas.size()), document);

        subtasks.add(generator2);
        subtasks.add(generator1);

        return subtasks;
    }
}
