package com.skillbox.searchengine.utils;

import com.skillbox.searchengine.entity.BaseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

public class BatchInsert<T extends BaseEntity> {
    private static final int BATCH_SIZE = 30;

    private List<BaseEntity> list;
    private JdbcTemplate jdbcTemplate;

    public BatchInsert(List<BaseEntity> list, JdbcTemplate jdbcTemplate) {
        this.list = list;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save() {
        List<List<BaseEntity>> list = new ArrayList<>(splitList());
        list.forEach(this::saveBatch);
    }

    private void saveBatch(List<BaseEntity> batch) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(batch.get(0).getSqlParams());

        batch.forEach(entry -> sqlBuilder.append(entry.getFieldsAsSQL()).append(','));
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);

        jdbcTemplate.execute(sqlBuilder.toString());
    }

    //splits this.list into batches of size set by BATCH_SIZE
    private List<List<BaseEntity>> splitList() {
        List<List<BaseEntity>> result = new ArrayList<>();
        if (list.size() > BATCH_SIZE) {
            int count = list.size() % BATCH_SIZE == 0 ?
                    list.size() / BATCH_SIZE :
                    list.size() / BATCH_SIZE + 1;
            for (int i = 0; i < count; i++) {
                int start = i * BATCH_SIZE;
                int end = Math.min(start + BATCH_SIZE, list.size());
                result.add(list.subList(start, end));
            }
        } else {
            result.add(list);
        }
        return result;
    }
}
