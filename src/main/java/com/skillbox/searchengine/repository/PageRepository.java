package com.skillbox.searchengine.repository;

import com.skillbox.searchengine.entity.Page;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class PageRepository {
    private static final SessionFactory sessionFactory = CustomRepository.getSessionFactory();

    private PageRepository(){}

    public static void save(Page page) {
        CustomRepository.save(page);
    }

    public static void save(Page page, Session session) {
        CustomRepository.save(page, session);
    }

    public static Page get(Integer pageId) {
        Page page = null;
        Transaction transaction = null;
        Session session = sessionFactory.openSession();

        try {
            transaction = session.beginTransaction();
            Query<Page> query = session.createQuery("from Page p join fetch p.site where p.id = :id", Page.class);
            query.setParameter("id", pageId);
            page = query.uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return page;
    }
}
