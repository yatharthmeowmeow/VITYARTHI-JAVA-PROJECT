package edu.ccrm.service;

import edu.ccrm.domain.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing enrollments.
 * Demonstrates complex business logic and exception handling.
 */
public class EnrollmentService {
    private final Map<String, Enrollment> enrollments; // Key: studentId-courseCode
    private final StudentService studentService;
    private final CourseService courseService;
    
    public EnrollmentService(StudentService studentService, CourseService courseService) {
        this.enrollments = new HashMap<>();
        this.studentService = Objects.requireNonNull(studentService, "StudentService cannot be null");
        this.courseService = Objects.requireNonNull(courseService, "CourseService cannot be null");
    }
    
    // Private method to generate enrollment key
    private String generateKey(String studentId, String courseCode) {
        return studentId + "-" + courseCode;
    }
    
    // Enroll student in course with business rule validation
    public void enrollStudent(String studentId, String courseCode) 
            throws DuplicateEnrollmentException, MaxCreditLimitExceededException {
        
        // Validate inputs
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        Objects.requireNonNull(courseCode, "Course code cannot be null");
        
        // Check if student exists
        Optional<Student> studentOpt = studentService.findById(studentId);
        if (studentOpt.isEmpty()) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }
        
        // Check if course exists
        Optional<Course> courseOpt = courseService.findById(courseCode);
        if (courseOpt.isEmpty()) {
            throw new IllegalArgumentException("Course not found: " + courseCode);
        }
        
        Student student = studentOpt.get();
        Course course = courseOpt.get();
        
        // Check if already enrolled (checked exception)
        String key = generateKey(studentId, courseCode);
        if (enrollments.containsKey(key)) {
            throw new DuplicateEnrollmentException(studentId, courseCode);
        }
        
        // Check course capacity
        if (course.isFull()) {
            throw new IllegalStateException("Course is at maximum capacity: " + courseCode);
        }
        
        // Check credit limit (unchecked exception)
        int currentCredits = student.getEnrolledCourses().size() * 3; // Assuming 3 credits per course
        int maxCredits = student.getMaxCreditsPerSemester();
        int courseCredits = course.getCredits();
        
        if (currentCredits + courseCredits > maxCredits) {
            throw new MaxCreditLimitExceededException(
                studentId, currentCredits, maxCredits, courseCredits);
        }
        
        // Perform enrollment
        Enrollment enrollment = new Enrollment(studentId, courseCode);
        enrollments.put(key, enrollment);
        
        // Update both student and course
        student.enrollInCourse(courseCode);
        course.enrollStudent(studentId);
    }
    
    // Unenroll student from course
    public boolean unenrollStudent(String studentId, String courseCode) {
        String key = generateKey(studentId, courseCode);
        Enrollment enrollment = enrollments.get(key);
        
        if (enrollment == null) {
            return false;
        }
        
        // Remove enrollment
        enrollments.remove(key);
        
        // Update student and course
        Optional<Student> studentOpt = studentService.findById(studentId);
        Optional<Course> courseOpt = courseService.findById(courseCode);
        
        if (studentOpt.isPresent()) {
            studentOpt.get().unenrollFromCourse(courseCode);
        }
        
        if (courseOpt.isPresent()) {
            courseOpt.get().unenrollStudent(studentId);
        }
        
        return true;
    }
    
    // Record grade for enrollment
    public void recordGrade(String studentId, String courseCode, Grade grade) {
        String key = generateKey(studentId, courseCode);
        Enrollment enrollment = enrollments.get(key);
        
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment not found for student " + studentId + " in course " + courseCode);
        }
        
        enrollment.setGrade(grade);
        
        // Update student's grade
        Optional<Student> studentOpt = studentService.findById(studentId);
        if (studentOpt.isPresent()) {
            studentOpt.get().setGrade(courseCode, grade);
        }
    }
    
    // Get enrollment
    public Optional<Enrollment> getEnrollment(String studentId, String courseCode) {
        String key = generateKey(studentId, courseCode);
        return Optional.ofNullable(enrollments.get(key));
    }
    
    // Get all enrollments for a student
    public List<Enrollment> getStudentEnrollments(String studentId) {
        return enrollments.values().stream()
                         .filter(e -> e.getStudentId().equals(studentId))
                         .collect(Collectors.toList());
    }
    
    // Get all enrollments for a course
    public List<Enrollment> getCourseEnrollments(String courseCode) {
        return enrollments.values().stream()
                         .filter(e -> e.getCourseCode().equals(courseCode))
                         .collect(Collectors.toList());
    }
    
    // Get all active enrollments
    public List<Enrollment> getActiveEnrollments() {
        return enrollments.values().stream()
                         .filter(Enrollment::isActive)
                         .collect(Collectors.toList());
    }
    
    // Get enrollments by grade
    public List<Enrollment> getEnrollmentsByGrade(Grade grade) {
        return enrollments.values().stream()
                         .filter(e -> e.getGrade() == grade)
                         .collect(Collectors.toList());
    }
    
    // Statistics methods
    public long getTotalEnrollments() {
        return enrollments.size();
    }
    
    public long getActiveEnrollmentCount() {
        return enrollments.values().stream()
                         .filter(Enrollment::isActive)
                         .count();
    }
    
    public Map<Grade, Long> getGradeDistribution() {
        return enrollments.values().stream()
                         .filter(Enrollment::hasGrade)
                         .collect(Collectors.groupingBy(
                             Enrollment::getGrade,
                             Collectors.counting()));
    }
    
    public double getPassingRate() {
        long total = enrollments.values().stream()
                               .filter(Enrollment::hasGrade)
                               .count();
        
        if (total == 0) return 0.0;
        
        long passing = enrollments.values().stream()
                                 .filter(Enrollment::isPassing)
                                 .count();
        
        return (double) passing / total * 100.0;
    }
    
    // Bulk operations
    public void withdrawStudent(String studentId) {
        enrollments.values().stream()
                   .filter(e -> e.getStudentId().equals(studentId))
                   .forEach(Enrollment::withdraw);
    }
    
    public void cancelCourse(String courseCode) {
        enrollments.values().stream()
                   .filter(e -> e.getCourseCode().equals(courseCode))
                   .forEach(e -> e.setActive(false));
    }
    
    // Restore enrollment from imported data
    public void restoreEnrollment(Enrollment enrollment) {
        Objects.requireNonNull(enrollment, "Enrollment cannot be null");
        
        String key = generateKey(enrollment.getStudentId(), enrollment.getCourseCode());
        enrollments.put(key, enrollment);
        
        // Update student and course references
        Optional<Student> studentOpt = studentService.findById(enrollment.getStudentId());
        Optional<Course> courseOpt = courseService.findById(enrollment.getCourseCode());
        
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.enrollInCourse(enrollment.getCourseCode());
            
            // If enrollment has a grade, set it
            if (enrollment.hasGrade()) {
                student.setGrade(enrollment.getCourseCode(), enrollment.getGrade());
            }
        }
        
        if (courseOpt.isPresent()) {
            try {
                courseOpt.get().enrollStudent(enrollment.getStudentId());
            } catch (IllegalStateException e) {
                // Course might be at capacity, but we're restoring data so allow it
                // In a real application, you might want to handle this differently
                System.err.println("Warning: Course capacity exceeded during restore: " + e.getMessage());
            }
        }
    }
}