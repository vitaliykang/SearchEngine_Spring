package com.skillbox.searchengine;

import com.skillbox.searchengine.entity.Lemma;
import com.skillbox.searchengine.entity.WebsiteIndex;
import com.skillbox.searchengine.repository.CustomRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;


public class Test {
    public static void main(String[] args) throws Exception{
        Lemma lemma = CustomRepository.findLemma("skillbox");
        System.out.println(lemma);
    }

    private static void workingSaveOrUpdate() {
        Session session = CustomRepository.getSessionFactory().openSession();

        Lemma lemma = new Lemma();
        lemma.setLemma("skillbox");
        lemma.setFrequency(1);

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(lemma);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("--Duplicate Entry--");
            lemma = session.createQuery("From Lemma where lemma = 'skillbox'", Lemma.class).uniqueResult();
            lemma.setFrequency(2);
            transaction = session.beginTransaction();
            session.update(lemma);
            transaction.commit();
        }

        session.close();
    }
}
