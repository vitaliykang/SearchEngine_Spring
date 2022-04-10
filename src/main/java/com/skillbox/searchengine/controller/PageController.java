package com.skillbox.searchengine.controller;

import com.skillbox.searchengine.Crawler;
import com.skillbox.searchengine.MySQLConnection;
import com.skillbox.searchengine.entity.Page;
import com.skillbox.searchengine.repository.PageRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@RestController
@RequestMapping(path="/")
public class PageController {
    @Autowired
    private PageRepository repository;

    @GetMapping(path="/")
    public Iterable<Page> loadAll() {
        String address = "https://stackoverflow.com/questions/39890849/what-exactly-is-field-injection-and-how-to-avoid-it";
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() - 1);
        Crawler crawler = new Crawler(Arrays.asList(address), repository);
        pool.invoke(crawler);

        return repository.findAll();
    }
}
