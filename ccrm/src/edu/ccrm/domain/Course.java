package edu.ccrm.domain;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Course class with Builder pattern implementation.
 * Demonstrates Builder design pattern and immutable-like construction.
 */
public class Course {
    private final String code;
    private String title;
    private int credits;
    private String instructorId;
    private Semester semester;
    private String department;
    private final Set<String> enrolledStudents; // Student IDs
    private int maxCapacity;
    private final LocalDateTime createdDate;
    private boolean active;
    
    // Private constructor - only accessible through Builder
    private Course(Builder builder) {
        this.code = builder.code;
        this.title = builder.title;
        this.credits = builder.credits;
        this.instructorId = builder.instructorId;
        this.semester = builder.semester;
        this.department = builder.department;
        this.enrolledStudents = new HashSet<>();
        this.maxCapacity = builder.maxCapacity;
        this.createdDate = LocalDateTime.now();
        this.active = true;
    }
    
    // Builder pattern implementation
    public static class Builder {
        private final String code; // Required
        private final String title; // Required
        private int credits = 3; // Default
        private String instructorId;
        private Semester semester = Semester.FALL; // Default
        private String department;
        private int maxCapacity = 50; // Default
        
        public Builder(String code, String title) {
            this.code = Objects.requireNonNull(code, "Course code cannot be null");
            this.title = Objects.requireNonNull(title, "Course title cannot be null");
        }
        
        public Builder credits(int credits) {
            if (credits <= 0) {
                throw new IllegalArgumentException("Credits must be positive");
            }
            this.credits = credits;
            return this;
        }
        
        public Builder instructor(String instructorId) {
            this.instructorId = instructorId;
            return this;
        }
        
        public Builder semester(Semester semester) {
            this.semester = Objects.requireNonNull(semester, "Semester cannot be null");
            return this;
        }
        
        public Builder department(String department) {
            this.department = department;
            return this;
        }
        
        public Builder maxCapacity(int maxCapacity) {
            if (maxCapacity <= 0) {
                throw new IllegalArgumentException("Max capacity must be positive");
            }
            this.maxCapacity = maxCapacity;
            return this;
        }
        
        public Course build() {
            // Validation before building
            if (code.trim().isEmpty()) {
                throw new IllegalArgumentException("Course code cannot be empty");
            }
            if (title.trim().isEmpty()) {
                throw new IllegalArgumentException("Course title cannot be empty");
            }
            return new Course(this);
        }
    }
    
    // Getters
    public String getCode() {
        return code;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
    }
    
    public int getCredits() {
        return credits;
    }
    
    public void setCredits(int credits) {
        if (credits <= 0) {
            throw new IllegalArgumentException("Credits must be positive");
        }
        this.credits = credits;
    }
    
    public String getInstructorId() {
        return instructorId;
    }
    
    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }
    
    public Semester getSemester() {
        return semester;
    }
    
    public void setSemester(Semester semester) {
        this.semester = Objects.requireNonNull(semester, "Semester cannot be null");
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public Set<String> getEnrolledStudents() {
        return new HashSet<>(enrolledStudents); // Defensive copy
    }
    
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be positive");
        }
        this.maxCapacity = maxCapacity;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    // Student enrollment methods
    public boolean enrollStudent(String studentId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        if (enrolledStudents.size() >= maxCapacity) {
            throw new IllegalStateException("Course is at maximum capacity");
        }
        return enrolledStudents.add(studentId);
    }
    
    public boolean unenrollStudent(String studentId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        return enrolledStudents.remove(studentId);
    }
    
    public boolean isStudentEnrolled(String studentId) {
        return enrolledStudents.contains(studentId);
    }
    
    public int getCurrentEnrollment() {
        return enrolledStudents.size();
    }
    
    public int getAvailableSpots() {
        return maxCapacity - enrolledStudents.size();
    }
    
    public boolean isFull() {
        return enrolledStudents.size() >= maxCapacity;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return Objects.equals(code, course.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
    
    @Override
    public String toString() {
        return String.format("Course{code='%s', title='%s', credits=%d, dept='%s', semester=%s, enrolled=%d/%d}", 
                           code, title, credits, department, semester, enrolledStudents.size(), maxCapacity);
    }
}