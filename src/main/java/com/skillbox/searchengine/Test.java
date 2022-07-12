package com.skillbox.searchengine;

import com.skillbox.searchengine.entity.Lemma;
import com.skillbox.searchengine.entity.Site;
import com.skillbox.searchengine.repository.CustomRepository;
import com.skillbox.searchengine.repository.SiteRepository;
import com.skillbox.searchengine.utils.AddressUtility;
import com.skillbox.searchengine.utils.Logger;
import com.skillbox.searchengine.utils.MultiIndexer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;


public class Test {
    public static void main(String[] args) {
        MultiIndexer indexer = new MultiIndexer(
                "https://bashorg.org", "http://bashorg.org");
        indexer.launch();
    }

    public static Site get(String name) {
        Session session = CustomRepository.getSessionFactory().openSession();
        Transaction transaction = null;
        Site result = null;
        try {
            transaction = session.beginTransaction();
            Query<Site> query = session.createQuery("from Site s where s.name = :name", Site.class);
            query.setParameter("name", name);
            result = query.uniqueResult();
            System.out.println(result == null);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("------Failed------");
        } finally {
            session.close();
        }

        return result;
    }
}
