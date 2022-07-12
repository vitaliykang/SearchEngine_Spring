package com.skillbox.searchengine.utils.SnippetGenerator;

import com.skillbox.searchengine.entity.Lemma;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

public class SnippetGenerator {
    private SnippetGenerator() {}

    /**
     * Puts together a snippet containing words from the provided lemma list and surrounding word.
     * @param requestLemmas - list of lemmas that make up a core for the snippet.
     * @param document - html code of a page, where the search is being conducted, in Jsoup Document format.
     * @return - snippet, containing search request lemmas and surrounding them words.
     */
    public static String generateSnippet(List<Lemma> requestLemmas, Document document) {
        ForkJoinPool pool = new ForkJoinPool();
        IndexMapGenerator mapGenerator = new IndexMapGenerator(requestLemmas, document);
        Map<Integer, Boolean> indexMap = pool.invoke(mapGenerator);

        StringBuilder result = new StringBuilder();
        String[] body = document.body().text().split(" ");

        indexMap.forEach((index, isSearchedWord) -> {
            if (isSearchedWord) {
                result.append(markSearchedWord(body[index]));
            } else {
                result.append(body[index]);
            }
            result.append(" ");
        });

        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    private static String markSearchedWord(String word) {
        return "<b>" + word + "</b>";
    }
}
