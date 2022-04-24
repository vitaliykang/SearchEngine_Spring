package com.skillbox.searchengine.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "page")
public class Page extends BaseEntity{
    private static final String SQL_PARAMS = "page (code, content, path) ";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "code", nullable = false)
    private Integer code;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Override
    public String getSqlParams() {
        return SQL_PARAMS;
    }

    @Override
    public String getFieldsAsSQL() {
        return String.format("VALUES (%d, '%s', '%s')", code, content, path);
    }

    //getters and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}