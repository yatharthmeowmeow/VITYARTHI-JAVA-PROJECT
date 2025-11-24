package edu.ccrm.service;

import java.util.List;
import java.util.function.Predicate;

/**
 * Generic interface for searchable operations.
 * Demonstrates interface with default methods and functional interfaces.
 */
public interface Searchable<T> {
    List<T> search(String query);
    List<T> filter(Predicate<T> predicate);
    
    // Default method to demonstrate diamond problem resolution
    default List<T> searchAndFilter(String query, Predicate<T> additionalFilter) {
        return search(query).stream()
                           .filter(additionalFilter)
                           .toList();
    }
    
    // Another default method
    default long countMatching(Predicate<T> predicate) {
        return filter(predicate).size();
    }
    
    // Static method in interface
    static <T> Predicate<T> combinePredicates(Predicate<T> first, Predicate<T> second) {
        return first.and(second);
    }
}