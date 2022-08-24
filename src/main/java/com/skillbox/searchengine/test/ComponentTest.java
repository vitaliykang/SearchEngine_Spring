package com.skillbox.searchengine.test;

import com.skillbox.searchengine.crudService.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//can we use @Autowired stuff
@Component
public class ComponentTest {
    @Autowired
    private SiteService siteService;

    public String test() {
        return siteService.find("test url").toString();
    }
}
