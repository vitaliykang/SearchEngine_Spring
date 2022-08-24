package com.skillbox.searchengine.crudService;

import com.skillbox.searchengine.crudRepo.FieldRepo;
import com.skillbox.searchengine.entity.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class FieldService {
    @Autowired
    private FieldRepo fieldRepo;

    public List<Field> findAll() {
        return fieldRepo.findAll();
    }
}
