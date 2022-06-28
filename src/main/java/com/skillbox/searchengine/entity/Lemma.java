package com.skillbox.searchengine.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "lemma", uniqueConstraints = @UniqueConstraint(columnNames = {"lemma", "site_id"}))
public class Lemma implements BaseEntity, BatchSavable, Comparable<Lemma>{
    private static final String SQL_PARAMS = "lemma (frequency, lemma, site_id) VALUES ";
    private static final String ENDING = " ON DUPLICATE KEY UPDATE frequency = frequency + 1";

    //todo add site to batch save

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "frequency", nullable = false)
    private Integer frequency;

    @Column(name = "lemma", nullable = false, length = 200)
    private String lemma;

    @Getter @Setter
    @Column(name = "site_id", nullable = false)
    private Integer siteId;

    @Override
    public String getHQLSelect() {
        return String.format("FROM Lemma WHERE lemma = '%s'", lemma);
    }

    @Override
    public String getFieldsAsSQL() {
        return String.format("(%d, '%s', %d)", frequency, lemma, siteId);
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

        return lemma.equals(lemma1.lemma) && siteId.equals(lemma1.siteId);
    }

    @Override
    public int hashCode() {
        return lemma.hashCode();
    }

    @Override
    public String toString() {
        return lemma;
    }

    @Override
    public int compareTo(Lemma otherLemma) {
        return frequency.compareTo(otherLemma.frequency);
    }
}