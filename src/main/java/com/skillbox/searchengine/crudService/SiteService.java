package com.skillbox.searchengine.crudService;

import com.skillbox.searchengine.crudRepo.SiteRepo;
import com.skillbox.searchengine.entity.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class SiteService {
    @Autowired
    private SiteRepo repository;

    public Site save(Site site) {
       return repository.save(site);
    }

    public void setStatus(String url, Site.Status status) {
        Site site = find(url);
        site.setStatus(status);
        site.setStatusTime(Instant.now());
        repository.flush();
    }

    public void flush() {
        repository.flush();
    }

    public Site find(String url) {
        Site probe = new Site();
        probe.setUrl(url);
        ExampleMatcher matcher = ExampleMatcher.matchingAny();
        Example<Site> example = Example.of(probe, matcher);
        List<Site> foundSites = repository.findAll(example);
        return foundSites.size() > 0 ? foundSites.get(0) : null;
    }

    public Site find(int id) {
        Site probe = new Site();
        probe.setId(id);
        ExampleMatcher matcher = ExampleMatcher.matchingAny();
        Example<Site> example = Example.of(probe, matcher);
        List<Site> list = repository.findAll(example);
        return list.size() == 1 ? list.get(0) : null;
    }

    public List<Site> findAll() {
        return repository.findAll();
    }
}
