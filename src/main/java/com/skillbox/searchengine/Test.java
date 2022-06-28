package com.skillbox.searchengine;

import com.skillbox.searchengine.utils.Crawler;
import com.skillbox.searchengine.utils.MultiIndexer;

import java.util.concurrent.ForkJoinPool;


public class Test {
    public static void main(String[] args) {
//        MultiIndexer indexer = new MultiIndexer("http://bashorg.org", "http://bash.org", "http://bashorg.org");
//        indexer.launch();
        MultiIndexer indexer = new MultiIndexer("http://bashorg.org");
        indexer.launch();
    }
}
