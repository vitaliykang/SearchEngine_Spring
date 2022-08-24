package com.skillbox.searchengine.controller;

import com.skillbox.searchengine.crudService.*;
import com.skillbox.searchengine.entity.Lemma;
import com.skillbox.searchengine.entity.Page;
import com.skillbox.searchengine.entity.Site;
import com.skillbox.searchengine.entity.WebsiteIndex;
import com.skillbox.searchengine.test.ComponentTest;
import com.skillbox.searchengine.utils.MultiIndexer;
import com.skillbox.searchengine.utils.SpringCrawlerWrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/")
public class TestController {
    @Autowired
    MultiIndexer indexer;
    @Autowired
    SiteService siteService;

    @GetMapping(path="/")
    public String test() throws IOException {
        indexer.launch();
        return "Done!";
    }


}
