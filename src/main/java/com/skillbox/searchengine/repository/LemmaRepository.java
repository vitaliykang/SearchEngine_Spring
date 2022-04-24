package com.skillbox.searchengine.repository;

import com.skillbox.searchengine.entity.Lemma;
import org.springframework.data.repository.CrudRepository;

public interface LemmaRepository extends CrudRepository<Lemma, Long> {
}
