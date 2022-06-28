package com.skillbox.searchengine.utils;

import com.skillbox.searchengine.entity.Site;
import com.skillbox.searchengine.repository.CustomRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class MultiIndexer {
    private List<String> websites = new ArrayList<>();

    public MultiIndexer(String... websites){
        for (int i = 0; i < websites.length; i++) {
            String url = websites[i];
            //adding '/' to the end of the address
            url = url.endsWith("/") || url.endsWith(".html") ?
                    url : url + "/";
            this.websites.add(url);
        }
    }

    public void launch() {
        HashSet<String> websiteSet = new HashSet<>(websites);
        websiteSet.forEach(url -> {
            Site site = new Site(url);
            CustomRepository.save(site);

            ForkJoinPool pool = new ForkJoinPool();
            Crawler crawler = new Crawler(List.of(url));
            pool.invoke(crawler);
        });
    }
}
