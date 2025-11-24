package edu.ccrm.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Enrollment class representing the relationship between a student and a course.
 * Demonstrates composition and data modeling.
 */
public class Enrollment {
    private final String studentId;
    private final String courseCode;
    private final LocalDateTime enrollmentDate;
    private Grade grade;
    private boolean active;
    private String notes;
    
    // Inner class for enrollment statistics
    public class EnrollmentInfo {
        public String getStudentInfo() {
            return "Student ID: " + studentId;
        }
        
        public String getCourseInfo() {
            return "Course: " + courseCode;
        }
        
        public String getEnrollmentStatus() {
            return active ? "Active" : "Inactive";
        }
        
        public String getGradeInfo() {
            return grade != null ? "Grade: " + grade : "Not graded";
        }
    }
    
    public Enrollment(String studentId, String courseCode) {
        this.studentId = Objects.requireNonNull(studentId, "Student ID cannot be null");
        this.courseCode = Objects.requireNonNull(courseCode, "Course code cannot be null");
        this.enrollmentDate = LocalDateTime.now();
        this.active = true;
    }
    
    public Enrollment(String studentId, String courseCode, LocalDateTime enrollmentDate) {
        this.studentId = Objects.requireNonNull(studentId, "Student ID cannot be null");
        this.courseCode = Objects.requireNonNull(courseCode, "Course code cannot be null");
        this.enrollmentDate = Objects.requireNonNull(enrollmentDate, "Enrollment date cannot be null");
        this.active = true;
    }
    
    // Getters
    public String getStudentId() {
        return studentId;
    }
    
    public String getCourseCode() {
        return courseCode;
    }
    
    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public Grade getGrade() {
        return grade;
    }
    
    public void setGrade(Grade grade) {
        this.grade = grade;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // Helper methods
    public boolean hasGrade() {
        return grade != null;
    }
    
    public boolean isPassing() {
        return grade != null && grade.isPassing();
    }
    
    public void withdraw() {
        this.active = false;
        this.notes = "Withdrawn on " + LocalDateTime.now();
    }
    
    // Create inner class instance
    public EnrollmentInfo getInfo() {
        return new EnrollmentInfo();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Enrollment that = (Enrollment) obj;
        return Objects.equals(studentId, that.studentId) &&
               Objects.equals(courseCode, that.courseCode);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseCode);
    }
    
    @Override
    public String toString() {
        return String.format("Enrollment{student='%s', course='%s', date=%s, grade=%s, active=%s}", 
                           studentId, courseCode, enrollmentDate.toLocalDate(), 
                           grade != null ? grade : "N/A", active);
    }
}