package com.skillbox.searchengine.repository;

import com.skillbox.searchengine.entity.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends CrudRepository<Page, Long> {
}
