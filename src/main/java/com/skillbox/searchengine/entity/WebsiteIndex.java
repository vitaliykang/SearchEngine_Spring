package com.skillbox.searchengine.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "website_index")
public class WebsiteIndex implements BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @Getter @Setter
    @Column(name = "lemma_id", nullable = false)
    private Integer lemmaId;

    @Column(name = "lemma_rank", nullable = false)
    private Double rank;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getRank() {
        return rank;
    }

    public void setRank(Double rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return String.format("lemma_id: %d, page_id: %d, rank: %f", lemmaId, page.getId(), rank);
    }

}