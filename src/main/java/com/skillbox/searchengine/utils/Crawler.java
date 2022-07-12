package com.skillbox.searchengine.utils;

import com.skillbox.searchengine.entity.Field;
import com.skillbox.searchengine.entity.Page;
import com.skillbox.searchengine.entity.Site;
import com.skillbox.searchengine.repository.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class Crawler extends RecursiveAction {
    //Contains pairs of [website url] - [set of unique links].
    /*Each time a new instance of Crawler is executed, siteMap is checked if it contains url that was passed into the
    crawler. If the url is present, all the links that are found by this crawler's instance are saved in the respective
    links set.
    */
    private static Map<String, Set<String>> siteMap = new HashMap<>();

    private static Set<String> allSites = new HashSet<>();

    private static HashSet<String> allLinks = new HashSet<>();

    @Getter @Setter
    private List<String> initAddress;
    @Getter @Setter
    private List<String> children;

    public Crawler(List<String> initAddress){
        this.initAddress = initAddress;
        children = new ArrayList<>();
        synchronized (Crawler.class) {
            allLinks.addAll(initAddress);
        }
    }

    public Crawler(String address) {
        this(List.of(address));
    }

    //main action
    private void process(List<String> list) {
        for (String url : list) {
            Elements linksOnPage = new Elements();
            Connection connection = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com");

            //main action
            Session session = CustomRepository.getSessionFactory().openSession();
            Document document = null;
            Connection.Response response = null;
            Integer statusCode = null;
            try {
                document = connection.get();
                response = connection.execute();
                statusCode = response.statusCode();

                linksOnPage = document.select("a[href]");

                //Retrieving site from the database using site name
                AddressUtility addressUtility = new AddressUtility(url);

                //todo replace with a proper logger
                Site site = SiteRepository.get(addressUtility.getRoot());
                if (site == null) {
                    Logger.log(url);
                    continue;
                }

                //New page database entry
                Page page = new Page();

                String path = addressUtility.getPath();

                System.out.println(url);
                page.setPath(path);
                page.setCode(statusCode);
                page.setContent(response.body());
                page.setSite(site);

                PageRepository.save(page, session);

                //extracting lemmas from given fields and saving them in repo
                Map<Integer, Double> idRanks = LemmaRepository.saveLemmas(document, site);

                //saving indexes
                IndexRepository.saveIndexes(page, idRanks);

                if (linksOnPage.size() > 0) {
                    String finalAddress = url;
                    linksOnPage.forEach(link -> {
                        String childAddress = link.attr("abs:href");
                        if (childAddress.startsWith(finalAddress)
                                && !childAddress.equalsIgnoreCase(finalAddress)
                                && !allLinks.contains(childAddress)
                                && !childAddress.startsWith("https://skillbox.ru/media/")
                                && ( childAddress.endsWith("/") || childAddress.endsWith(".html") )) {

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            synchronized (Crawler.class) {
                                allLinks.add(childAddress); //adding the child link address to the allLinks pool
                            }
                            children.add(childAddress);
                        }
                    });
                }
            } catch (IOException e) {
                PageRepository.save(new Page(url, 404));
            }
        }
    }

    @Override
    protected void compute() {
        if (initAddress.size() > 1) {
            ForkJoinTask.invokeAll(splitTask());
        } else {
            process(initAddress);

            // if there are child links, set initial address to children and split the task
            if (this.children.size() > 0) {
                this.setInitAddress(children);
                ForkJoinTask.invokeAll(splitTask());
            }
        }
    }

    private List<Crawler> splitTask(){
        List<Crawler> subtasks = new ArrayList<>();
        int half = initAddress.size() / 2;

        Crawler crawler1 = new Crawler(initAddress.subList(0, half));
        Crawler crawler2 = new Crawler(initAddress.subList(half, initAddress.size()));

        subtasks.add(crawler1);
        subtasks.add(crawler2);

        return subtasks;
    }
}
