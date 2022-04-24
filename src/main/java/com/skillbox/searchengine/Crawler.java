package com.skillbox.searchengine;

import com.skillbox.searchengine.entity.Field;
import com.skillbox.searchengine.entity.Page;
import com.skillbox.searchengine.repository.PageRepository;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class Crawler extends RecursiveAction {
    private static HashSet<String> allLinks = new HashSet<>();
    @Getter @Setter
    private static List<Page> pages = new ArrayList<>();

    @Getter @Setter
    private List<String> initAddress;
    @Getter @Setter
    private List<String> children;
    private PageRepository repository;

    public Crawler(List<String> initAddress, PageRepository repository){
        this.initAddress = initAddress;
        children = new ArrayList<>();
        allLinks.addAll(initAddress);
        this.repository = repository;
    }

    //main action
    private void process(List<String> list){
        list.forEach(address -> {
            Elements linksOnPage = new Elements();
            try {
                //adding '/' to the end of the address
                address = address.endsWith("/") || address.endsWith(".html") ?
                        address : address + "/";

                Connection connection = Jsoup.connect(address)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com");

                //main action
                Document doc = connection.get();
                Connection.Response response = connection.execute();
                int statusCode = response.statusCode();

                linksOnPage = doc.select("a[href]");

                //New page database entry
                Page page = new Page();

                int index = 0;
                for (int i = 0; i < 3; i++) {
                    index = address.indexOf('/', index + 1);
                }
                String path = address.substring(index);

                page.setPath(path);
                page.setCode(statusCode);
                page.setContent(response.body());
                pages.add(page);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (linksOnPage.size() > 0) {
                String finalAddress = address;
                linksOnPage.forEach(link -> {
                    String childAddress = link.attr("abs:href");
                    if (childAddress.startsWith(finalAddress)
                            && !childAddress.equalsIgnoreCase(finalAddress)
                            && !allLinks.contains(childAddress)
                            && !childAddress.startsWith("https://skillbox.ru/media/")
                            && ( childAddress.endsWith("/") || childAddress.endsWith(".html") )) {

                        System.out.println(childAddress);

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        allLinks.add(childAddress); //adding the child link address to the allLinks pool
                        children.add(childAddress);
                    }
                });
            }
        });
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

        Crawler fetcher1 = new Crawler(initAddress.subList(0, half), repository);
        Crawler fetcher2 = new Crawler(initAddress.subList(half, initAddress.size()), repository);

        subtasks.add(fetcher1);
        subtasks.add(fetcher2);

        return subtasks;
    }
}
