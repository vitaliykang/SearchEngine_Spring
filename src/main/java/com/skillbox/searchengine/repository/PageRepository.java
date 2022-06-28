package com.skillbox.searchengine.repository;

import com.skillbox.searchengine.entity.Page;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class PageRepository {
    private static SessionFactory sessionFactory = CustomRepository.getSessionFactory();

    public static Page get(Integer pageId) {
        Page page;
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query<Page> query = session.createQuery("from Page p join fetch p.site where p.id = :id", Page.class);
            query.setParameter("id", pageId);
            page = query.uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }

        return page;
    }
}
