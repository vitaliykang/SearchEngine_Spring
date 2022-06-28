package com.skillbox.searchengine.entity;

import com.skillbox.searchengine.utils.AddressUtility;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "site")
public class Site implements BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "status_time", nullable = false)
    private Instant statusTime;

    @Lob
    @Column(name = "last_error")
    private String lastError;

    @Column(name = "url", nullable = false, length = 200)
    private String url;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    public Site() {}

    public Site(String url) {
        AddressUtility addressUtility = new AddressUtility(url);
        setName(addressUtility.getSiteName());
        setUrl(addressUtility.getUrl());
        setStatus(Site.Status.INDEXING);
        setStatusTime(Instant.now());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Instant statusTime) {
        this.statusTime = statusTime;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum Status {
        INDEXING, INDEXED, FAILED
    }

}