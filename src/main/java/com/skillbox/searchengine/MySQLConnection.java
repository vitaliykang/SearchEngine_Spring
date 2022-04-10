package com.skillbox.searchengine;

import com.skillbox.searchengine.entity.Page;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;
import java.util.Map;

public class MySQLConnection {
    private static SessionFactory sessionFactory;
    static {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        sessionFactory = metadata.getSessionFactoryBuilder().build();
    }

    private MySQLConnection() {
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void updateLemmas(Map<String, Integer> lemmas) {
        if (lemmas.size() > 50) {

        }
    }

    //work in progress
    private static void multiInsert(List<String> list) {
        StringBuilder batch = new StringBuilder();
        list.forEach(lemma -> {
            String insert = String.format("(1, '%s'),", lemma);
            batch.append(insert);
        });
        batch.deleteCharAt(batch.length()-1);

        String query = String.format("INSERT INTO lemma VALUES%s ON DUPLICATE KEY UPDATE frequency = frequency + 1", batch);
        Session session = sessionFactory.openSession();
    }
}
