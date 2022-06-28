package com.skillbox.searchengine.controller;

import com.skillbox.searchengine.utils.Crawler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

@RestController
@RequestMapping(path="/")
public class PageController {

    @GetMapping(path="/")
    public String test() {
        String address = "https://skillbox.ru/";
        ForkJoinPool pool = new ForkJoinPool();
        Crawler crawler = new Crawler(List.of(address));
        pool.invoke(crawler);

        return "Done!";
    }


}
