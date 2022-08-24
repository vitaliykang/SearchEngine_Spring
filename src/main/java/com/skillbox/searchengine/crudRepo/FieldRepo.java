package com.skillbox.searchengine.crudRepo;

import com.skillbox.searchengine.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepo extends JpaRepository<Field, Long> {
}
