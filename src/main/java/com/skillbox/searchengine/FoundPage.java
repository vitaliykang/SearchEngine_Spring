package com.skillbox.searchengine;

import com.skillbox.searchengine.entity.Lemma;
import com.skillbox.searchengine.entity.Page;
import com.skillbox.searchengine.utils.SnippetGenerator.SnippetGenerator;
import lombok.Getter;

import java.util.List;

public class FoundPage {
    @Getter
    private String uri;
    @Getter
    private String title;
    @Getter
    private String snippet;
    @Getter
    private Double relevance;

    public FoundPage() {
    }

    public FoundPage(Page page, Double relevance, List<Lemma> requestLemmas) {
        uri = page.getPath();
        title = page.getDocument().title();
        snippet = SnippetGenerator.generateSnippet(requestLemmas, page.getDocument());
        this.relevance = relevance;
    }

    public String toString() {
        return String.format("%s - %f %n%s %n", title, relevance, snippet);
    }
}
