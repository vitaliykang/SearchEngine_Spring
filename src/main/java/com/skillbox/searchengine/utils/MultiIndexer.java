package com.skillbox.searchengine.utils;

import com.skillbox.searchengine.connectionProperties.Properties;
import com.skillbox.searchengine.crudService.IndexService;
import com.skillbox.searchengine.crudService.LemmaService;
import com.skillbox.searchengine.crudService.PageService;
import com.skillbox.searchengine.crudService.SiteService;
import com.skillbox.searchengine.entity.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@Service
public class MultiIndexer {
    @Autowired private SiteService siteService;
    @Autowired private PageService pageService;
    @Autowired private LemmaService lemmaService;
    @Autowired private IndexService indexService;

    //deprecated
    private List<String> websites = new ArrayList<>();

    public void launch() {
        List<String> validSites = validateUrls();

        //launching SpringCrawler
        for (String validURL : validSites) {
            SpringCrawler.setSiteService(siteService);
            SpringCrawler.setPageService(pageService);
            SpringCrawler.setLemmaService(lemmaService);
            SpringCrawler.setIndexService(indexService);

            SpringCrawler crawler = new SpringCrawler(validURL);
            ForkJoinPool pool = new ForkJoinPool();
            pool.invoke(crawler);
            siteService.setStatus(validURL, Site.Status.INDEXED);
        }
    }

    private List<String> validateUrls() {
        List<String> validSites = new ArrayList<>();
        for (Map.Entry<String, String> entry : Properties.getSites().entrySet()) {
            String url = entry.getKey();
            url = url.endsWith("/") || url.endsWith(".html") ?
                    url : url + "/";
            //check if database has an entry with such url, else create a new Site entity
            Site site = siteService.find(url);
            if (site == null) {
                site = new Site(url, entry.getValue());
                site = siteService.save(site);
            }
            //testing connection
            int responseCode = testConnection(url);
            if (responseCode == 200) {
                validSites.add(url);
            } else {
                site.setStatusTime(Instant.now());
                site.setStatus(Site.Status.FAILED);
                if (responseCode == 0) {
                    site.setLastError("404");
                } else {
                    site.setLastError(responseCode + "");
                }
            }
            siteService.flush();
        }

        return validSites;
    }

    public static int testConnection(String url) {
        HttpURLConnection connection = null;
        int code = 0;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            code = connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return code;
    }
}
