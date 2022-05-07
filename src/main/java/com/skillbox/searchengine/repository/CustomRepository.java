package com.skillbox.searchengine.repository;

import com.skillbox.searchengine.entity.BaseEntity;
import com.skillbox.searchengine.entity.BatchSavable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.*;

public class CustomRepository {
    private static final int BATCH_SIZE = 30;

    private static SessionFactory sessionFactory;
    static {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        sessionFactory = metadata.getSessionFactoryBuilder().build();
    }

    private CustomRepository() {}

    public static <T> void dropTable(Class<T> tClass) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        String query = String.format("DROP TABLE IF EXISTS %s", tClass.getSimpleName().toLowerCase(Locale.ROOT));
        session.createSQLQuery(query).executeUpdate();

        transaction.commit();
        session.close();
    }

    public static <T extends BaseEntity> void save(T t) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(t);
        System.out.print("Saving ");
        t.printInfo();
        transaction.commit();
        session.close();
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
     * Saves a collection of BaseEntities in the database.
     * @param collection
     * @param <T>
     */
    public static synchronized <T extends BaseEntity> void saveAll(Collection<T> collection) {
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
        } else {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            list.forEach(session::save);
            transaction.commit();
            session.close();
        }
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
        Transaction transaction = session.beginTransaction();

        session.createSQLQuery(sqlBuilder.toString()).executeUpdate();

        transaction.commit();
        session.close();
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
