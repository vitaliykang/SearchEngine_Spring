package com.skillbox.searchengine.utils;

import com.skillbox.searchengine.entity.Field;
import com.skillbox.searchengine.entity.Lemma;
import com.skillbox.searchengine.entity.Page;
import com.skillbox.searchengine.entity.WebsiteIndex;
import com.skillbox.searchengine.repository.CustomRepository;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class Crawler extends RecursiveAction {
    private static HashSet<String> allLinks = new HashSet<>();

    @Getter @Setter
    private List<String> initAddress;
    @Getter @Setter
    private List<String> children;
    private List<Field> fields;

    public Crawler(List<String> initAddress){
        this.initAddress = initAddress;
        children = new ArrayList<>();
        allLinks.addAll(initAddress);
        fields = CustomRepository.findAll(Field.class);
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

                String path = extractPath(address);

                System.out.println(address);
                page.setPath(path);
                page.setCode(statusCode);
                page.setContent(response.body());
                CustomRepository.save(page);

                //extracting lemmas from given fields and saving them in repo
                Map<Lemma, Double> lemmaMap = PageProcessor.generateMap(doc);
                lemmaMap.forEach((lemma, rank) -> {
                    System.out.println(lemma);
                    WebsiteIndex index = new WebsiteIndex();

                    index.setRank(rank);
                    index.setLemma(lemma);
                    index.setPage(page);

                    try {
                        CustomRepository.saveUnique(lemma);
                    } catch (Exception exception) {
                        //increment frequency and update lemma if it already exists
                        int newFrequency = lemma.getFrequency();
                        String param = lemma.getLemma();

                        Session session = CustomRepository.getSessionFactory().openSession();
                        Transaction transaction = session.beginTransaction();
                        Query<Lemma> query = session.createQuery("From Lemma l where l.lemma =: param");
                        query.setParameter("lemma", param);
                        Lemma oldLemma = query.uniqueResult();
                        transaction.commit();

                        transaction = session.beginTransaction();
                        oldLemma.setFrequency(oldLemma.getFrequency() + newFrequency);
                        session.update(oldLemma);
                        transaction.commit();

                        session.close();
                    }
                    CustomRepository.save(index);
                });


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

    /**
     *Extracts path from the provided web address
     */
    private String extractPath(String address) {
        int index = 0;
        for (int i = 0; i < 3; i++) {
            index = address.indexOf('/', index + 1);
        }
        String path = address.substring(index);
        return path;
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
