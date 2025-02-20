package com.carvalhotechsolutions.mundoanimal.repositories;

import java.util.List;

public interface BaseRepository<T> {
    T save(T entity);
    T findById(Long id);
    List<T> findAll();
    void deleteById(Long id);
}

