package com.skillbox.searchengine.utils;

import com.skillbox.searchengine.crudService.IndexService;
import com.skillbox.searchengine.crudService.LemmaService;
import com.skillbox.searchengine.crudService.PageService;
import com.skillbox.searchengine.crudService.SiteService;
import com.skillbox.searchengine.entity.Page;
import com.skillbox.searchengine.entity.Site;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class SpringCrawler extends RecursiveAction {
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

    @Getter @Setter
    private static SiteService siteService;
    @Getter @Setter
    private static PageService pageService;
    @Getter @Setter
    private static LemmaService lemmaService;
    @Getter @Setter
    private static IndexService indexService;

    public SpringCrawler(List<String> initAddress){
        this.initAddress = initAddress;
        children = new ArrayList<>();
        synchronized (SpringCrawler.class) {
            allLinks.addAll(initAddress);
        }
    }

    public SpringCrawler(String address) {
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
                Site site = siteService.find(addressUtility.getHomePageURL());
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

                pageService.save(page);

                //extracting lemmas from given fields and saving them in repo
                Map<Integer, Double> idRanks = lemmaService.saveLemmas(document, site);

                //saving indexes
                indexService.saveIndexes(page, idRanks);

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

                            synchronized (SpringCrawler.class) {
                                allLinks.add(childAddress); //adding the child link address to the allLinks pool
                            }
                            children.add(childAddress);
                        }
                    });
                }
            } catch (IOException e) {
                pageService.save(new Page(url, 404));
            }
        }
    }

    @Override
    protected void compute() {
        if (initAddress.size() > 1) {
        } else {
            process(initAddress);

            // if there are child links, set initial address to children and split the task
            if (this.children.size() > 0) {
                this.setInitAddress(children);
            }
        }
    }

}
