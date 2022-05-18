package com.skillbox.searchengine.utils;

import com.skillbox.searchengine.entity.Lemma;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SearchRequestParser {
    private String request;

    public SearchRequestParser(String request) {
        this.request = request;
    }

    public void parse() {
        List<Lemma> requestLemmas = new ArrayList<>(LemmaCounter.countLemmas(request).keySet());
        requestLemmas.forEach(requestLemma -> {

        });
    }
}
