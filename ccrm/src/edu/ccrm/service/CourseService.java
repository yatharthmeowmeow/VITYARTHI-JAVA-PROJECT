package edu.ccrm.service;

import edu.ccrm.domain.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class for managing courses.
 * Demonstrates interface implementation and stream operations.
 */
public class CourseService implements Persistable<Course>, Searchable<Course> {
    private final Map<String, Course> courses;
    
    public CourseService() {
        this.courses = new HashMap<>();
    }
    
    @Override
    public void save(Course course) throws Exception {
        Objects.requireNonNull(course, "Course cannot be null");
        if (courses.containsKey(course.getCode())) {
            throw new IllegalArgumentException("Course with code " + course.getCode() + " already exists");
        }
        courses.put(course.getCode(), course);
    }
    
    @Override
    public void saveAll(List<Course> courseList) throws Exception {
        for (Course course : courseList) {
            save(course);
        }
    }
    
    @Override
    public Optional<Course> findById(String code) {
        return Optional.ofNullable(courses.get(code));
    }
    
    @Override
    public List<Course> findAll() {
        return new ArrayList<>(courses.values());
    }
    
    @Override
    public void delete(String code) {
        courses.remove(code);
    }
    
    @Override
    public void deleteAll() {
        courses.clear();
    }
    
    @Override
    public boolean exists(String code) {
        return courses.containsKey(code);
    }
    
    @Override
    public long count() {
        return courses.size();
    }
    
    @Override
    public List<Course> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }
        
        String lowerQuery = query.toLowerCase();
        return courses.values().stream()
                     .filter(course -> 
                         course.getCode().toLowerCase().contains(lowerQuery) ||
                         course.getTitle().toLowerCase().contains(lowerQuery) ||
                         (course.getDepartment() != null && 
                          course.getDepartment().toLowerCase().contains(lowerQuery)))
                     .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> filter(Predicate<Course> predicate) {
        return courses.values().stream()
                     .filter(predicate)
                     .collect(Collectors.toList());
    }
    
    // Business methods
    public void updateCourse(String code, Course updatedCourse) throws Exception {
        if (!courses.containsKey(code)) {
            throw new IllegalArgumentException("Course with code " + code + " not found");
        }
        // Remove old and add new (since code might change)
        courses.remove(code);
        courses.put(updatedCourse.getCode(), updatedCourse);
    }
    
    public void deactivateCourse(String code) {
        Course course = courses.get(code);
        if (course != null) {
            course.setActive(false);
        }
    }
    
    public void activateCourse(String code) {
        Course course = courses.get(code);
        if (course != null) {
            course.setActive(true);
        }
    }
    
    // Search and filter methods using Stream API
    public List<Course> findByInstructor(String instructorId) {
        return filter(course -> Objects.equals(course.getInstructorId(), instructorId));
    }
    
    public List<Course> findByDepartment(String department) {
        return filter(course -> Objects.equals(course.getDepartment(), department));
    }
    
    public List<Course> findBySemester(Semester semester) {
        return filter(course -> course.getSemester() == semester);
    }
    
    public List<Course> findByCredits(int credits) {
        return filter(course -> course.getCredits() == credits);
    }
    
    public List<Course> findActiveCourses() {
        return filter(Course::isActive);
    }
    
    public List<Course> findAvailableCourses() {
        return filter(course -> course.isActive() && !course.isFull());
    }
    
    // Statistics methods
    public Map<String, Long> getDepartmentDistribution() {
        return courses.values().stream()
                     .filter(course -> course.getDepartment() != null)
                     .collect(Collectors.groupingBy(
                         Course::getDepartment,
                         Collectors.counting()));
    }
    
    public Map<Semester, Long> getSemesterDistribution() {
        return courses.values().stream()
                     .collect(Collectors.groupingBy(
                         Course::getSemester,
                         Collectors.counting()));
    }
    
    public double getAverageEnrollment() {
        return courses.values().stream()
                     .mapToInt(Course::getCurrentEnrollment)
                     .average()
                     .orElse(0.0);
    }
    
    public List<Course> getMostPopularCourses(int limit) {
        return courses.values().stream()
                     .sorted((c1, c2) -> Integer.compare(c2.getCurrentEnrollment(), c1.getCurrentEnrollment()))
                     .limit(limit)
                     .collect(Collectors.toList());
    }
    
    public int getTotalCreditsOffered() {
        return courses.values().stream()
                     .filter(Course::isActive)
                     .mapToInt(Course::getCredits)
                     .sum();
    }
}