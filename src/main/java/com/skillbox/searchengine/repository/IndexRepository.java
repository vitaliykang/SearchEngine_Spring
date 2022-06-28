package com.skillbox.searchengine.repository;

import com.skillbox.searchengine.entity.Lemma;
import com.skillbox.searchengine.entity.Page;
import com.skillbox.searchengine.entity.WebsiteIndex;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Map;

public class IndexRepository {
    private static final SessionFactory sessionFactory = CustomRepository.getSessionFactory();

    //idRanks map is a result of LemmaRepository.saveLemmas method
    public static void saveIndexes(Page page, Map<Integer, Double> idRanks) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            idRanks.forEach((id, rank) -> {
                WebsiteIndex index = new WebsiteIndex();
                index.setLemmaId(id);
                index.setRank(rank);
                index.setPage(page);
                session.save(index);
            });
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public static List<WebsiteIndex> findIndexesByLemma(Lemma lemma) {
        Transaction transaction = null;
        List<WebsiteIndex> result = null;
        if (lemma.getId() != null) {
            try (Session session = sessionFactory.openSession()) {
                transaction = session.beginTransaction();

                Query<WebsiteIndex> query = session.createQuery(
                        "select i " +
                                "from WebsiteIndex i " +
                                "join fetch i.page " +
                                "where i.lemmaId = :lemmaId", WebsiteIndex.class);
                query.setParameter("lemmaId", lemma.getId());
                result = query.getResultList();

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
            }
        }
        return result;
    }

    public static WebsiteIndex findIndexByLemmaAndPage(Lemma lemma, Page page) {
        WebsiteIndex result = null;
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query<WebsiteIndex> query = session.createQuery("from WebsiteIndex i join fetch i.page where i.lemmaId = :lemmaId and i.page = :page", WebsiteIndex.class);
            query.setParameter("lemmaId", lemma.getId());
            query.setParameter("page", page);
            result = query.uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
        return result;
    }
}
