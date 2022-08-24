package com.skillbox.searchengine.utils;

import com.skillbox.searchengine.crudService.IndexService;
import com.skillbox.searchengine.crudService.LemmaService;
import com.skillbox.searchengine.crudService.PageService;
import com.skillbox.searchengine.crudService.SiteService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ForkJoinPool;

@Service
public class SpringCrawlerWrapper {
    @Autowired private SiteService siteService;
    @Autowired private PageService pageService;
    @Autowired private LemmaService lemmaService;
    @Autowired private IndexService indexService;

    public void launch(String url) {
        SpringCrawler.setSiteService(siteService);
        SpringCrawler.setPageService(pageService);
        SpringCrawler.setLemmaService(lemmaService);
        SpringCrawler.setIndexService(indexService);

        SpringCrawler crawler = new SpringCrawler(url);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(crawler);
    }
}
