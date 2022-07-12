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
    @Getter
    private static final SessionFactory sessionFactory;

    static {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        sessionFactory = metadata.getSessionFactoryBuilder().build();
    }

    private CustomRepository() {
    }

    static <T extends BaseEntity> void save(T t, Session session) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(t);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    static <T extends BaseEntity> void save(T t) {
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            session.persist(t);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    static <T extends BaseEntity> List<T> findAll(Class<T> tClass) {
        Transaction transaction = null;
        List<T> result = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            String query = String.format("from %s", tClass.getSimpleName());
            result = session.createQuery(query, tClass).getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }
}
