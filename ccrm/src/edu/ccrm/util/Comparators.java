package edu.ccrm.util;

import edu.ccrm.domain.*;
import java.util.Comparator;
import java.util.function.Function;

/**
 * Utility class containing various comparators using lambdas and method references.
 * Demonstrates functional interfaces, lambdas, and comparator chaining.
 */
public final class Comparators {
    
    // Private constructor to prevent instantiation
    private Comparators() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    // Student Comparators
    public static final Comparator<Student> BY_NAME = 
        Comparator.comparing(student -> student.getFullName().getFullName());
    
    public static final Comparator<Student> BY_REG_NO = 
        Comparator.comparing(Student::getRegNo);
    
    public static final Comparator<Student> BY_EMAIL = 
        Comparator.comparing(Student::getEmail);
    
    public static final Comparator<Student> BY_GPA = 
        Comparator.comparing(Student::calculateGPA);
    
    public static final Comparator<Student> BY_GPA_DESCENDING = 
        BY_GPA.reversed();
    
    public static final Comparator<Student> BY_STUDENT_ENROLLMENT_DATE = 
        Comparator.comparing(Student::getEnrollmentDate);
    
    public static final Comparator<Student> BY_COURSE_COUNT = 
        Comparator.comparing(student -> student.getEnrolledCourses().size());
    
    // Course Comparators
    public static final Comparator<Course> BY_CODE = 
        Comparator.comparing(Course::getCode);
    
    public static final Comparator<Course> BY_TITLE = 
        Comparator.comparing(Course::getTitle);
    
    public static final Comparator<Course> BY_CREDITS = 
        Comparator.comparing(Course::getCredits);
    
    public static final Comparator<Course> BY_DEPARTMENT = 
        Comparator.comparing(Course::getDepartment, Comparator.nullsLast(String::compareTo));
    
    public static final Comparator<Course> BY_SEMESTER = 
        Comparator.comparing(Course::getSemester);
    
    public static final Comparator<Course> BY_ENROLLMENT_COUNT = 
        Comparator.comparing(Course::getCurrentEnrollment);
    
    public static final Comparator<Course> BY_AVAILABILITY = 
        Comparator.comparing(Course::getAvailableSpots);
    
    // Instructor Comparators
    public static final Comparator<Instructor> BY_NAME_INSTRUCTOR = 
        Comparator.comparing(instructor -> instructor.getFullName().getFullName());
    
    public static final Comparator<Instructor> BY_EMPLOYEE_ID = 
        Comparator.comparing(Instructor::getEmployeeId);
    
    public static final Comparator<Instructor> BY_DEPARTMENT_INSTRUCTOR = 
        Comparator.comparing(Instructor::getDepartment);
    
    public static final Comparator<Instructor> BY_COURSE_LOAD = 
        Comparator.comparing(Instructor::getCourseLoad);
    
    public static final Comparator<Instructor> BY_HIRE_DATE = 
        Comparator.comparing(Instructor::getHireDate);
    
    // Enrollment Comparators
    public static final Comparator<Enrollment> BY_ENROLLMENT_DATE_ENR = 
        Comparator.comparing(Enrollment::getEnrollmentDate);
    
    public static final Comparator<Enrollment> BY_STUDENT_ID = 
        Comparator.comparing(Enrollment::getStudentId);
    
    public static final Comparator<Enrollment> BY_COURSE_CODE = 
        Comparator.comparing(Enrollment::getCourseCode);
    
    public static final Comparator<Enrollment> BY_GRADE = 
        Comparator.comparing(Enrollment::getGrade, Comparator.nullsLast(Enum::compareTo));
    
    // Complex/Composite Comparators
    public static final Comparator<Student> BY_NAME_THEN_REG_NO = 
        BY_NAME.thenComparing(BY_REG_NO);
    
    public static final Comparator<Course> BY_DEPARTMENT_THEN_CODE = 
        BY_DEPARTMENT.thenComparing(BY_CODE);
    
    public static final Comparator<Student> BY_GPA_THEN_NAME = 
        BY_GPA_DESCENDING.thenComparing(BY_NAME);
    
    // Functional comparator factory methods
    public static <T> Comparator<T> comparing(Function<T, String> keyExtractor, boolean ignoreCase) {
        if (ignoreCase) {
            return Comparator.comparing(keyExtractor, String.CASE_INSENSITIVE_ORDER);
        }
        return Comparator.comparing(keyExtractor);
    }
    
    public static Comparator<Student> byNameIgnoreCase() {
        return comparing(student -> student.getFullName().getFullName(), true);
    }
    
    public static Comparator<Course> byTitleIgnoreCase() {
        return comparing(Course::getTitle, true);
    }
    
    // Lambda-based custom comparators
    public static final Comparator<Student> BY_ACADEMIC_STANDING = (s1, s2) -> {
        double gpa1 = s1.calculateGPA();
        double gpa2 = s2.calculateGPA();
        
        // Custom academic standing logic
        String standing1 = getAcademicStanding(gpa1);
        String standing2 = getAcademicStanding(gpa2);
        
        int standingComparison = standing1.compareTo(standing2);
        if (standingComparison != 0) {
            return standingComparison;
        }
        
        // If same standing, compare by GPA
        return Double.compare(gpa2, gpa1); // Higher GPA first
    };
    
    private static String getAcademicStanding(double gpa) {
        if (gpa >= 9.0) return "Excellent";
        if (gpa >= 8.0) return "Good";
        if (gpa >= 7.0) return "Average";
        if (gpa >= 6.0) return "Below Average";
        return "Poor";
    }
    
    // Method to create reverse comparator
    public static <T> Comparator<T> reversed(Comparator<T> comparator) {
        return comparator.reversed();
    }
    
    // Method to create null-safe comparator
    public static <T extends Comparable<T>> Comparator<T> nullsLast() {
        return Comparator.nullsLast(Comparator.naturalOrder());
    }
    
    public static <T extends Comparable<T>> Comparator<T> nullsFirst() {
        return Comparator.nullsFirst(Comparator.naturalOrder());
    }
}