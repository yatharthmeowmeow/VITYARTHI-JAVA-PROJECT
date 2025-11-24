package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.*;

/**
 * Student class extending Person.
 * Demonstrates inheritance, polymorphism, and collections.
 */
public class Student extends Person {
    private String regNo;
    private final Set<String> enrolledCourses; // Course codes
    private final Map<String, Grade> grades; // Course code -> Grade
    private LocalDate enrollmentDate;
    private int maxCreditsPerSemester;
    
    // Static nested class for student statistics
    public static class StudentStats {
        private final double gpa;
        private final int totalCredits;
        private final int completedCourses;
        
        public StudentStats(double gpa, int totalCredits, int completedCourses) {
            this.gpa = gpa;
            this.totalCredits = totalCredits;
            this.completedCourses = completedCourses;
        }
        
        public double getGpa() { return gpa; }
        public int getTotalCredits() { return totalCredits; }
        public int getCompletedCourses() { return completedCourses; }
        
        @Override
        public String toString() {
            return String.format("GPA: %.2f, Credits: %d, Courses: %d", 
                               gpa, totalCredits, completedCourses);
        }
    }
    
    public Student(String id, String regNo, Name fullName, String email, LocalDate dateOfBirth) {
        super(id, fullName, email, dateOfBirth);
        this.regNo = Objects.requireNonNull(regNo, "Registration number cannot be null");
        this.enrolledCourses = new HashSet<>();
        this.grades = new HashMap<>();
        this.enrollmentDate = LocalDate.now();
        this.maxCreditsPerSemester = 24; // Default max credits
    }
    
    // Copy constructor for defensive copying
    public Student(Student other) {
        super(other.getId(), other.getFullName(), other.getEmail(), other.getDateOfBirth());
        this.regNo = other.regNo;
        this.enrolledCourses = new HashSet<>(other.enrolledCourses);
        this.grades = new HashMap<>(other.grades);
        this.enrollmentDate = other.enrollmentDate;
        this.maxCreditsPerSemester = other.maxCreditsPerSemester;
        this.setActive(other.isActive());
    }
    
    @Override
    public String getRole() {
        return "Student";
    }
    
    @Override
    public String getDisplayInfo() {
        return String.format("Student: %s (Reg: %s) - %d courses enrolled", 
                           getFullName(), regNo, enrolledCourses.size());
    }
    
    // Getters and setters
    public String getRegNo() {
        return regNo;
    }
    
    public void setRegNo(String regNo) {
        this.regNo = Objects.requireNonNull(regNo, "Registration number cannot be null");
    }
    
    public Set<String> getEnrolledCourses() {
        return new HashSet<>(enrolledCourses); // Defensive copy
    }
    
    public Map<String, Grade> getGrades() {
        return new HashMap<>(grades); // Defensive copy
    }
    
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
    
    public int getMaxCreditsPerSemester() {
        return maxCreditsPerSemester;
    }
    
    public void setMaxCreditsPerSemester(int maxCreditsPerSemester) {
        if (maxCreditsPerSemester <= 0) {
            throw new IllegalArgumentException("Max credits must be positive");
        }
        this.maxCreditsPerSemester = maxCreditsPerSemester;
    }
    
    // Course enrollment methods
    public boolean enrollInCourse(String courseCode) {
        Objects.requireNonNull(courseCode, "Course code cannot be null");
        return enrolledCourses.add(courseCode);
    }
    
    public boolean unenrollFromCourse(String courseCode) {
        Objects.requireNonNull(courseCode, "Course code cannot be null");
        boolean removed = enrolledCourses.remove(courseCode);
        if (removed) {
            grades.remove(courseCode); // Remove grade if unenrolled
        }
        return removed;
    }
    
    public boolean isEnrolledIn(String courseCode) {
        return enrolledCourses.contains(courseCode);
    }
    
    // Grade management
    public void setGrade(String courseCode, Grade grade) {
        Objects.requireNonNull(courseCode, "Course code cannot be null");
        Objects.requireNonNull(grade, "Grade cannot be null");
        if (!enrolledCourses.contains(courseCode)) {
            throw new IllegalArgumentException("Student not enrolled in course: " + courseCode);
        }
        grades.put(courseCode, grade);
    }
    
    public Grade getGrade(String courseCode) {
        return grades.get(courseCode);
    }
    
    public boolean hasGrade(String courseCode) {
        return grades.containsKey(courseCode);
    }
    
    // Calculate GPA
    public double calculateGPA() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        
        double totalPoints = grades.values().stream()
                                  .mapToDouble(Grade::getGradePoint)
                                  .sum();
        return totalPoints / grades.size();
    }
    
    // Get student statistics
    public StudentStats getStatistics() {
        return new StudentStats(
            calculateGPA(),
            enrolledCourses.size() * 3, // Assuming 3 credits per course
            grades.size()
        );
    }
    
    @Override
    public String toString() {
        return String.format("Student{id='%s', regNo='%s', name='%s', courses=%d, gpa=%.2f}", 
                           getId(), regNo, getFullName(), enrolledCourses.size(), calculateGPA());
    }
}