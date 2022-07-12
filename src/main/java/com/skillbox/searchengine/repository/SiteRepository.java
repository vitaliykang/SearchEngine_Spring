package com.skillbox.searchengine.repository;

import com.skillbox.searchengine.entity.Site;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.Instant;

public class SiteRepository {
    private static SessionFactory sessionFactory = CustomRepository.getSessionFactory();

    private SiteRepository(){}

    public static void save(Site site) {
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            Query<Site> query = session.createQuery("from Site s where s.url = :url", Site.class);
            query.setParameter("url", site.getUrl());
            Site repoSite = query.uniqueResult();

            if (repoSite == null) {
                session.persist(site);
            } else {
                repoSite.setStatus(Site.Status.INDEXING);
                repoSite.setStatusTime(Instant.now());
            }

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

    public static Site get(Integer id) {
        Site result = null;
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            Query<Site> query = session.createQuery("from Site s where s.id = :id", Site.class);
            query.setParameter("id", id);
            result = query.uniqueResult();
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

    public static Site get(String url) {
        Transaction  transaction = null;
        Site result = null;
        Session session = sessionFactory.openSession();

        try {
            transaction = session.beginTransaction();
            Query<Site> query = session.createQuery("from Site s where s.url = :url", Site.class);
            query.setParameter("url", url);
            result = query.uniqueResult();
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

    public static void updateStatus(String url, Site.Status status) {
        Transaction transaction = null;
        Session session = sessionFactory.openSession();

        try {
            transaction = session.beginTransaction();
            Query<Site> query = session.createQuery("from Site s where s.url = :url", Site.class);
            query.setParameter("url", url);
            Site site = query.uniqueResult();
            site.setStatus(status);
            site.setStatusTime(Instant.now());
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
}
