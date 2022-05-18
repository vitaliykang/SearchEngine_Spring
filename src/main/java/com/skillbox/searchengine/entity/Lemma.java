package com.skillbox.searchengine.entity;

import org.hibernate.annotations.SQLInsert;

import javax.persistence.*;

@Entity
@Table(name = "lemma")
public class Lemma implements BaseEntity, BatchSavable, Unique{
    private static final String SQL_PARAMS = "lemma (frequency, lemma) VALUES ";
    private static final String ENDING = " ON DUPLICATE KEY UPDATE frequency = frequency + 1";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "frequency", nullable = false)
    private Integer frequency;

    @Column(name = "lemma", nullable = false, length = 200, unique = true)
    private String lemma;

    @Override
    public String getHQLSelect() {
        return String.format("FROM Lemma WHERE lemma = '%s'", lemma);
    }

    @Override
    public String getFieldsAsSQL() {
        return String.format("(%d, '%s')", frequency, lemma);
    }

    @Override
    public String getSqlParams() {
        return SQL_PARAMS;
    }

    @Override
    public String getEnding() {
        return ENDING;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lemma lemma1 = (Lemma) o;

        return lemma.equals(lemma1.lemma);
    }

    @Override
    public int hashCode() {
        return lemma.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Lemma info: %d) %s (frequency = %d)", id, lemma, frequency);
    }

    @Override
    public String getUniqueField() {
        return lemma;
    }
}