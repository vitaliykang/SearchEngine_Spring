package com.skillbox.searchengine.utils;

import com.skillbox.searchengine.entity.Site;
import com.skillbox.searchengine.repository.CustomRepository;
import com.skillbox.searchengine.repository.SiteRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.*;
import java.time.Instant;
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
        List<String> validSites = new ArrayList<>();

        //pinging the sites
        for (String url : websiteSet) {
            Site site = new Site(url);
            if (testConnection(url)) {
                validSites.add(url);
            } else {
                site.setStatus(Site.Status.FAILED);
            }
            SiteRepository.save(site);
        }

        for (String url : validSites) {
            Crawler crawler = new Crawler(url);
            ForkJoinPool pool = new ForkJoinPool();
            pool.invoke(crawler);
            SiteRepository.updateStatus(url, Site.Status.INDEXED);
        }
    }

    public static boolean testConnection(String url) {
        HttpURLConnection connection = null;
        int code = 0;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            code = connection.getResponseCode();
            // You can determine on HTTP return code received. 200 is success.
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return code == 200;
    }
}
