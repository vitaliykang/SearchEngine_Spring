package com.skillbox.searchengine.crudRepo;

import com.skillbox.searchengine.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepo extends JpaRepository<Page, Long> {
}
