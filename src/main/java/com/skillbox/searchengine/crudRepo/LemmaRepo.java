package com.skillbox.searchengine.crudRepo;

import com.skillbox.searchengine.entity.Lemma;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public interface LemmaRepo extends JpaRepository<Lemma, Long> {

}
