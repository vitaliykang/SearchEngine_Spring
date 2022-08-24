package com.skillbox.searchengine.crudService;

import com.skillbox.searchengine.crudRepo.PageRepo;
import com.skillbox.searchengine.entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class PageService {
    @Autowired
    private PageRepo pageRepo;

    public void save(Page page) {
        pageRepo.save(page);
    }

    public Page find(String path, int siteId) {
        Page probe = new Page();
        probe.setPath(path);
        ExampleMatcher matcher = ExampleMatcher.matchingAny();
        Example<Page> example = Example.of(probe, matcher);
        List<Page> pageList = pageRepo.findAll(example);
        if (pageList.size() > 0) {
            for (Page page : pageList) {
                if (page.getSite().getId()
                        .equals(siteId)) {
                    return page;
                }
            }
        }
        return null;
    }

    public Page find(int id) {
        Page probe = new Page();
        probe.setId(id);
        ExampleMatcher matcher = ExampleMatcher.matchingAny();
        Example<Page> example = Example.of(probe, matcher);
        List<Page> pageList = pageRepo.findAll(example);
        if (pageList.size() == 1) {
            return pageList.get(0);
        }
        return null;
    }
}
