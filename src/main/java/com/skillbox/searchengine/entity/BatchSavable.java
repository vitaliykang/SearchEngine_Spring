package com.skillbox.searchengine.entity;

public interface BatchSavable {
    String getFieldsAsSQL();
    String getSqlParams();
    String getEnding();
    String getHQLSelect();
    Integer getId();
}
