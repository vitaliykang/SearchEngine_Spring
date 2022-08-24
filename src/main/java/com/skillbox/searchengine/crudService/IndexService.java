package com.skillbox.searchengine.crudService;

import com.skillbox.searchengine.crudRepo.IndexRepo;
import com.skillbox.searchengine.entity.Lemma;
import com.skillbox.searchengine.entity.Page;
import com.skillbox.searchengine.entity.WebsiteIndex;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class IndexService {
    @Autowired
    private IndexRepo indexRepo;

    //idRanks map is a result of LemmaRepository.saveLemmas method
    public void saveIndexes(Page page, Map<Integer, Double> idRanks) {
        idRanks.forEach((id, rank) -> {
            WebsiteIndex index = new WebsiteIndex();
            index.setLemmaId(id);
            index.setRank(rank);
            index.setPage(page);

            indexRepo.save(index);
        });
    }

    public List<WebsiteIndex> findIndexesByLemma(Lemma lemma) {
        WebsiteIndex probe = new WebsiteIndex();
        probe.setLemmaId(lemma.getId());
        ExampleMatcher matcher = ExampleMatcher.matchingAny();
        Example<WebsiteIndex> example = Example.of(probe, matcher);
        return indexRepo.findAll(example);
    }

    public WebsiteIndex findIndexByLemmaAndPage(Lemma lemma, Page page) {
        WebsiteIndex probe = new WebsiteIndex();
        probe.setPage(page);
        ExampleMatcher matcher = ExampleMatcher.matchingAny();
        Example<WebsiteIndex> example = Example.of(probe, matcher);
        List<WebsiteIndex> indexList = indexRepo.findAll(example);
        for (WebsiteIndex index : indexList) {
            if (index.getLemmaId()
                    .equals(lemma.getId())) {
                return index;
            }
        }
        return null;
    }

    public List<WebsiteIndex> findAll(Example<WebsiteIndex> example) {
        return indexRepo.findAll(example);
    }
}
