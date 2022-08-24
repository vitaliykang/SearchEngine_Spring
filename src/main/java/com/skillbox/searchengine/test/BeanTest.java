package com.skillbox.searchengine.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

//can we use @Autowired stuff without using @Component
public class BeanTest {
    @Autowired


    @Bean
    public BeanTest beanTest() {
        return null;
    }
}
