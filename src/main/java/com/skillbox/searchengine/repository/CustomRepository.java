package com.skillbox.searchengine.repository;

import com.skillbox.searchengine.entity.*;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.sql.Connection;
import java.util.*;

public class CustomRepository {
    private static final int BATCH_SIZE = 30;

    @Getter
    private static SessionFactory sessionFactory;
    private static Session uSession;
    private static Connection uConnection;

    static {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        sessionFactory = metadata.getSessionFactoryBuilder().build();

        uSession = sessionFactory.openSession();
        uConnection = uSession.disconnect();
    }

    private CustomRepository() {}

    public static void saveIndexes (Page page, Map<Integer, Double> idRanks) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()){
            transaction = session.beginTransaction();
            idRanks.forEach((id, rank) ->{
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

    public static <T extends BaseEntity> Integer save(T t, Session session) {
        Transaction transaction = session.beginTransaction();
        Integer id = (Integer) session.save(t);
        transaction.commit();
        return id;
    }

    public static Lemma findLemma(String lemmaStr) {
        Lemma result = null;
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            String query = String.format("From Lemma where lemma = '%s'", lemmaStr);
            result = session.createQuery(query, Lemma.class).uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
        return result;
    }

    public static <T extends BaseEntity> List<T> findAll(Class<T> tClass) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        String query = String.format("from %s", tClass.getSimpleName());
        List<T> result = session.createQuery(query, tClass).getResultList();

        transaction.commit();
        session.close();
        return result;
    }

    /**
     * Saves map of lemma-rank into the database.
     * @param lemmaRankMap map of lemma objects and their respective ranks
     * @return map containing ids of saved lemmas and their ranks
     */
    public static synchronized Map<Integer, Double> saveLemmas(Map<Lemma, Double> lemmaRankMap) {
        Map<Integer, Double> idRankMap = new HashMap<>();
        List<Lemma> lemmas = new ArrayList<>(lemmaRankMap.keySet());
        batchInsert(lemmas);
        lemmas.forEach(lemma ->
           idRankMap.put(findId(lemma), lemmaRankMap.get(lemma)));
        return idRankMap;
    }

    /**
     * Saves a collection of BaseEntities in the database.
     * @param collection
     * @param <T>
     */
    public static synchronized <T extends BaseEntity> List<Integer> saveAll(Collection<T> collection) {
        List<Integer> ids = null;
        //converting collection into a list
        List<T> list;
        if (collection instanceof List) {
            list = (List<T>) collection;
        } else {
            list = new ArrayList<>(collection);
        }

        //saving the elements in the database: in batches if supported, or one by one otherwise
        if (list.get(0) instanceof BatchSavable) {
            List<BatchSavable> batchList = new ArrayList<>();
            list.forEach(entry -> batchList.add((BatchSavable) entry));
            batchInsert(batchList);
            ids = new ArrayList<>();
            for (BatchSavable element : batchList) {
                ids.add(findId(element));
            }
        } else {
            ids = new ArrayList<>();
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            for (BaseEntity entity : list) {
                ids.add((Integer) session.save(entity));
            }
            transaction.commit();
            session.close();
        }
        return ids;
    }

    private static <T extends BatchSavable> void batchInsert(List<T> list) {
        if (list.size() > BATCH_SIZE) {
            List<List<T>> batches = splitList(list);
            batches.forEach(CustomRepository::saveBatch);
        }
        else {
            saveBatch(list);
        }
    }

    private static <T extends BatchSavable> void saveBatch(List<T> batch) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(batch.get(0).getSqlParams());

        for (T entry : batch) {
            sqlBuilder.append(entry.getFieldsAsSQL()).append(',');
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(batch.get(0).getEnding());

        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.createSQLQuery(sqlBuilder.toString()).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
        session.close();
    }

    private static <T extends BatchSavable> Integer findId(T t) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        Integer result = null;

        try {
            transaction = session.beginTransaction();
            BatchSavable object = (BatchSavable) session.createQuery(t.getHQLSelect()).uniqueResult();
            result = object.getId();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
        return result;
    }

    private static <T extends BatchSavable> List<List<T>> splitList(List<T> list) {
        List<List<T>> result = new ArrayList<>();
        if (list.size() > BATCH_SIZE) {
            int count = list.size() % BATCH_SIZE == 0 ?
                    list.size() / BATCH_SIZE :
                    list.size() / BATCH_SIZE + 1;
            for (int i = 0; i < count; i++) {
                int start = i * BATCH_SIZE;
                int end = Math.min(start + BATCH_SIZE, list.size());
                result.add(list.subList(start, end));
            }
        } else {
            result.add(list);
        }
        return result;
    }
}
