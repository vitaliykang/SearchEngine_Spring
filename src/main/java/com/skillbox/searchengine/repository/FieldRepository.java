package com.skillbox.searchengine.repository;

import com.skillbox.searchengine.entity.Field;

import java.util.List;

public class FieldRepository {
    public static List<Field> findAll() {
        return CustomRepository.findAll(Field.class);
    }
}
