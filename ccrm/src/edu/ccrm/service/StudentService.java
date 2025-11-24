package edu.ccrm.service;

import edu.ccrm.domain.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class for managing students.
 * Demonstrates interface implementation, polymorphism, and streams.
 */
public class StudentService implements Persistable<Student>, Searchable<Student> {
    private final Map<String, Student> students;
    
    public StudentService() {
        this.students = new HashMap<>();
    }
    
    @Override
    public void save(Student student) throws Exception {
        Objects.requireNonNull(student, "Student cannot be null");
        if (students.containsKey(student.getId())) {
            throw new IllegalArgumentException("Student with ID " + student.getId() + " already exists");
        }
        students.put(student.getId(), new Student(student)); // Defensive copy
    }
    
    @Override
    public void saveAll(List<Student> studentList) throws Exception {
        for (Student student : studentList) {
            save(student);
        }
    }
    
    @Override
    public Optional<Student> findById(String id) {
        Student student = students.get(id);
        return student != null ? Optional.of(new Student(student)) : Optional.empty();
    }
    
    @Override
    public List<Student> findAll() {
        return students.values().stream()
                      .map(Student::new) // Create defensive copies
                      .collect(Collectors.toList());
    }
    
    @Override
    public void delete(String id) {
        students.remove(id);
    }
    
    @Override
    public void deleteAll() {
        students.clear();
    }
    
    @Override
    public boolean exists(String id) {
        return students.containsKey(id);
    }
    
    @Override
    public long count() {
        return students.size();
    }
    
    @Override
    public List<Student> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }
        
        String lowerQuery = query.toLowerCase();
        return students.values().stream()
                      .filter(student -> 
                          student.getFullName().getFullName().toLowerCase().contains(lowerQuery) ||
                          student.getRegNo().toLowerCase().contains(lowerQuery) ||
                          student.getEmail().toLowerCase().contains(lowerQuery))
                      .map(Student::new)
                      .collect(Collectors.toList());
    }
    
    @Override
    public List<Student> filter(Predicate<Student> predicate) {
        return students.values().stream()
                      .filter(predicate)
                      .map(Student::new)
                      .collect(Collectors.toList());
    }
    
    // Business methods
    public void updateStudent(String id, Student updatedStudent) throws Exception {
        if (!students.containsKey(id)) {
            throw new IllegalArgumentException("Student with ID " + id + " not found");
        }
        students.put(id, new Student(updatedStudent));
    }
    
    public void deactivateStudent(String id) {
        Student student = students.get(id);
        if (student != null) {
            student.deactivate();
        }
    }
    
    public void activateStudent(String id) {
        Student student = students.get(id);
        if (student != null) {
            student.activate();
        }
    }
    
    public Optional<Student> findByRegNo(String regNo) {
        return students.values().stream()
                      .filter(s -> s.getRegNo().equals(regNo))
                      .findFirst()
                      .map(Student::new);
    }
    
    public List<Student> findActiveStudents() {
        return filter(Student::isActive);
    }
    
    public List<Student> findStudentsEnrolledInCourse(String courseCode) {
        return filter(student -> student.isEnrolledIn(courseCode));
    }
    
    public List<Student> findTopStudentsByGPA(int limit) {
        return students.values().stream()
                      .sorted((s1, s2) -> Double.compare(s2.calculateGPA(), s1.calculateGPA()))
                      .limit(limit)
                      .map(Student::new)
                      .collect(Collectors.toList());
    }
    
    // Statistics methods using streams
    public double getAverageGPA() {
        return students.values().stream()
                      .mapToDouble(Student::calculateGPA)
                      .average()
                      .orElse(0.0);
    }
    
    public long countActiveStudents() {
        return students.values().stream()
                      .filter(Student::isActive)
                      .count();
    }
    
    public Map<Integer, Long> getEnrollmentDistribution() {
        return students.values().stream()
                      .collect(Collectors.groupingBy(
                          s -> s.getEnrolledCourses().size(),
                          Collectors.counting()));
    }
}