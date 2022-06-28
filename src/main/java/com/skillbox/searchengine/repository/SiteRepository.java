package com.skillbox.searchengine.repository;

import com.skillbox.searchengine.entity.Site;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.FileWriter;
import java.io.IOException;

public class SiteRepository {
    private static SessionFactory sessionFactory = CustomRepository.getSessionFactory();

    public static Site get(String siteName) {
        Transaction transaction = null;
        Site site = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query<Site> query = session.createQuery("from Site s where s.name = :siteName", Site.class);
            query.setParameter("siteName", siteName);
            site = query.uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
//            throw e;
            try (FileWriter writer = new FileWriter("src/main/resources/output.txt")) {
                writer.write(e.getMessage());
                writer.write("Site name: " + siteName);
            } catch (IOException ioException) {

            }
        }
        return site;
    }

    public static Site get(Integer id) {
        Site site = null;
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query<Site> query = session.createQuery("from Site s where s.id = :id");
            query.setParameter("id", id);
            site = query.uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }

        return site;
    }
}
