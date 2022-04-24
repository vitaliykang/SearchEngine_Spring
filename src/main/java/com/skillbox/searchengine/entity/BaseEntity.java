package com.skillbox.searchengine.entity;

import java.util.List;

public abstract class BaseEntity {
    public abstract String getFieldsAsSQL();
    public abstract String getSqlParams();
}
