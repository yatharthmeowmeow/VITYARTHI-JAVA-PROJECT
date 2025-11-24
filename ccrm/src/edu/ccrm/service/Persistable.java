package edu.ccrm.service;

import java.util.List;
import java.util.Optional;

/**
 * Generic interface for persistence operations.
 * Demonstrates interface design and generics.
 */
public interface Persistable<T> {
    void save(T entity) throws Exception;
    void saveAll(List<T> entities) throws Exception;
    Optional<T> findById(String id);
    List<T> findAll();
    void delete(String id);
    void deleteAll();
    boolean exists(String id);
    long count();
}