package com.skillbox.searchengine.controller;

import com.skillbox.searchengine.Crawler;
import com.skillbox.searchengine.entity.Field;
import com.skillbox.searchengine.entity.Page;
import com.skillbox.searchengine.repository.FieldRepository;
import com.skillbox.searchengine.repository.LemmaRepository;
import com.skillbox.searchengine.repository.PageRepository;
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
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private FieldRepository fieldRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private List<String> fields = new ArrayList<>();

    {
        fieldRepository.findAll().forEach(field -> fields.add(field.getSelector()));
    }

    @GetMapping(path="/")
    public String loadAll() {
        if (Crawler.getPages().size() > 0) {
            Crawler.setPages(new ArrayList<>());
        }

        String address = "https://skillbox.ru/";
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() - 1);
        Crawler crawler = new Crawler(List.of(address), pageRepository);
        pool.invoke(crawler);

        List<Page> pages = Crawler.getPages();

        //creating a batch insert
        //todo create buffered batched insert
        String sql = "INSERT INTO page (code, content, path) VALUES ";
        StringBuilder batch = new StringBuilder(sql);
        pages.forEach(page -> {
           String pageInfo = String.format("(%d, '%s', '%s'),", page.getCode(), page.getContent(), page.getPath());
           batch.append(pageInfo);
        });
        batch.deleteCharAt(batch.length()-1);

        jdbcTemplate.execute(batch.toString());

        return "Done";
    }
}
