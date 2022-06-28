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

    private CustomRepository() {
    }

    public static <T extends BaseEntity> Integer save(T t, Session session) {
        Transaction transaction = session.beginTransaction();
        Integer id = (Integer) session.save(t);
        transaction.commit();
        return id;
    }

    public static <T extends BaseEntity> Integer save(T t) {
        Transaction transaction = null;
        Integer id = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            id = (Integer) session.save(t);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }

        return id;
    }

    public static <T extends BaseEntity> List<T> findAll(Class<T> tClass) {
        Transaction transaction = null;
        List<T> result = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            String query = String.format("from %s", tClass.getSimpleName());
            result = session.createQuery(query, tClass).getResultList();
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
