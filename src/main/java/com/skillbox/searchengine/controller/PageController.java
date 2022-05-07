package com.skillbox.searchengine.controller;

import com.skillbox.searchengine.entity.BaseEntity;
import com.skillbox.searchengine.entity.Lemma;
import com.skillbox.searchengine.repository.CustomRepository;
import com.skillbox.searchengine.utils.BatchInsert;
import com.skillbox.searchengine.utils.Crawler;
import com.skillbox.searchengine.entity.Page;
import com.skillbox.searchengine.repository.FieldRepository;
import com.skillbox.searchengine.repository.LemmaRepository;
import com.skillbox.searchengine.repository.PageRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@RestController
@RequestMapping(path="/")
public class PageController {

    @GetMapping(path="/")
    public String test() {
        String address = "https://skillbox.ru/";
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() - 1);
        Crawler crawler = new Crawler(List.of(address));
        pool.invoke(crawler);

        return "Done!";
    }

//    @GetMapping(path="/")
//    public String loadAll() {
//        if (Crawler.getPages().size() > 0) {
//            Crawler.setPages(new ArrayList<>());
//        }
//
//        String address = "https://skillbox.ru/";
//        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() - 1);
//        Crawler crawler = new Crawler(List.of(address), pageRepository);
//        pool.invoke(crawler);
//
//        List<Page> pages = Crawler.getPages();
//
//        BatchInsert<Page> pageBatchInsert = new BatchInsert<>(pages, jdbcTemplate);
//        pageBatchInsert.save();
//
//        return "Done";
//    }

}
