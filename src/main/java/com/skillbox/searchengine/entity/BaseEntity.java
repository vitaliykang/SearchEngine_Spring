package com.skillbox.searchengine.entity;

import java.util.List;

public abstract class BaseEntity {
    abstract String getFieldsAsSQL();
    abstract String getSqlParams();
}
