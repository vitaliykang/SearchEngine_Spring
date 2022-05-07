package com.skillbox.searchengine.utils;

import com.skillbox.searchengine.entity.BaseEntity;
import com.skillbox.searchengine.entity.BatchSavable;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

public class BatchInsert<T extends BatchSavable> {
    private static final int BATCH_SIZE = 1;

    //todo rewrite batch insert
    public BatchInsert() {

    }

    /**
     * Saves this.list in batches of size set by BATCH_SIZE.
     */
    public void save(List<T> list) {
        List<List<T>> batches = new ArrayList<>(splitList(list));
        batches.forEach(this::saveBatch);
    }

    private void saveBatch(List<T> batch) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(batch.get(0).getSqlParams());

        batch.forEach(entry -> sqlBuilder.append(entry.getFieldsAsSQL()).append(','));
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);

//        jdbcTemplate.execute(sqlBuilder.toString());
    }

    //splits this.list into batches of size set by BATCH_SIZE
    private List<List<T>> splitList(List<T> list) {
        List<List<T>> result = new ArrayList<>();
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
