package com.skillbox.searchengine.crudRepo;

import com.skillbox.searchengine.entity.WebsiteIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexRepo extends JpaRepository<WebsiteIndex, Long> {
}
