package edu.ccrm.util;

import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Utility class for validation operations.
 * Demonstrates input validation, regex patterns, and assertion usage.
 */
public final class Validators {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    // Student registration number pattern (example: 23CSE001)
    private static final Pattern REG_NO_PATTERN = 
        Pattern.compile("^\\d{2}[a-zA-Z]{3}\\d{5}$");
    
    // Course code pattern (example: CSE101, MATH201)
    private static final Pattern COURSE_CODE_PATTERN = 
        Pattern.compile("^[A-Z]{3,4}\\d{3}$");
    
    // Employee ID pattern (example: EMP001, INST123)
    private static final Pattern EMPLOYEE_ID_PATTERN = 
        Pattern.compile("^[A-Z]{3,4}\\d{3,5}$");
    
    // Private constructor to prevent instantiation
    private Validators() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    // Email validation
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    // Student registration number validation
    public static boolean isValidRegistrationNumber(String regNo) {
        return regNo != null && REG_NO_PATTERN.matcher(regNo).matches();
    }
    
    // Course code validation
    public static boolean isValidCourseCode(String courseCode) {
        return courseCode != null && COURSE_CODE_PATTERN.matcher(courseCode).matches();
    }
    
    // Employee ID validation
    public static boolean isValidEmployeeId(String employeeId) {
        return employeeId != null && EMPLOYEE_ID_PATTERN.matcher(employeeId).matches();
    }
    
    // Name validation
    public static boolean isValidName(String name) {
        return name != null && 
               !name.trim().isEmpty() && 
               name.trim().length() >= 2 && 
               name.trim().length() <= 50 &&
               name.matches("^[a-zA-Z\\s'-]+$");
    }
    
    // ID validation (alphanumeric, 3-20 characters)
    public static boolean isValidId(String id) {
        return id != null && 
               id.matches("^[a-zA-Z0-9]{1,20}$");
    }
    
    // Date validation (not in future, not too old)
    public static boolean isValidDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return false;
        
        LocalDate now = LocalDate.now();
        LocalDate minDate = now.minusYears(100); // Not older than 100 years
        
        return !dateOfBirth.isAfter(now) && !dateOfBirth.isBefore(minDate);
    }
    
    // Credits validation
    public static boolean isValidCredits(int credits) {
        return credits > 0 && credits <= 10; // Reasonable range
    }
    
    // Capacity validation
    public static boolean isValidCapacity(int capacity) {
        return capacity > 0 && capacity <= 500; // Reasonable range
    }
    
    // GPA validation
    public static boolean isValidGPA(double gpa) {
        return gpa >= 0.0 && gpa <= 10.0; // 10-point scale
    }
    
    // Percentage validation
    public static boolean isValidPercentage(double percentage) {
        return percentage >= 0.0 && percentage <= 100.0;
    }
    
    // String validation methods
    public static boolean isNonEmptyString(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    public static boolean isValidStringLength(String str, int minLength, int maxLength) {
        if (str == null) return false;
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    // Validation with assertions (demonstrate assertion usage)
    public static void validateStudentData(String id, String regNo, String email, 
                                         LocalDate dateOfBirth) {
        // Using assertions for invariant checking
        assert id != null : "Student ID cannot be null";
        assert regNo != null : "Registration number cannot be null";
        assert email != null : "Email cannot be null";
        assert dateOfBirth != null : "Date of birth cannot be null";
        
        // Additional validation
        if (!isValidId(id)) {
            throw new IllegalArgumentException("Invalid student ID format: " + id);
        }
        
        if (!isValidRegistrationNumber(regNo)) {
            throw new IllegalArgumentException("Invalid registration number format: " + regNo);
        }
        
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        
        if (!isValidDateOfBirth(dateOfBirth)) {
            throw new IllegalArgumentException("Invalid date of birth: " + dateOfBirth);
        }
    }
    
    public static void validateCourseData(String code, String title, int credits, int maxCapacity) {
        // Assertions for preconditions
        assert code != null : "Course code cannot be null";
        assert title != null : "Course title cannot be null";
        assert credits > 0 : "Credits must be positive";
        assert maxCapacity > 0 : "Max capacity must be positive";
        
        if (!isValidCourseCode(code)) {
            throw new IllegalArgumentException("Invalid course code format: " + code);
        }
        
        if (!isValidStringLength(title, 3, 100)) {
            throw new IllegalArgumentException("Course title must be 3-100 characters: " + title);
        }
        
        if (!isValidCredits(credits)) {
            throw new IllegalArgumentException("Invalid credits value: " + credits);
        }
        
        if (!isValidCapacity(maxCapacity)) {
            throw new IllegalArgumentException("Invalid capacity value: " + maxCapacity);
        }
    }
    
    // Composite validation methods
    public static ValidationResult validateStudent(String id, String regNo, String firstName, 
                                                 String lastName, String email, LocalDate dateOfBirth) {
        ValidationResult result = new ValidationResult();
        
        if (!isValidId(id)) {
            result.addError("Invalid student ID format");
        }
        
        if (!isValidRegistrationNumber(regNo)) {
            result.addError("Invalid registration number format");
        }
        
        if (!isValidName(firstName)) {
            result.addError("Invalid first name");
        }
        
        if (!isValidName(lastName)) {
            result.addError("Invalid last name");
        }
        
        if (!isValidEmail(email)) {
            result.addError("Invalid email format");
        }
        
        if (!isValidDateOfBirth(dateOfBirth)) {
            result.addError("Invalid date of birth");
        }
        
        return result;
    }
    
    // Inner class for validation results
    public static class ValidationResult {
        private final java.util.List<String> errors;
        
        public ValidationResult() {
            this.errors = new java.util.ArrayList<>();
        }
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public boolean isValid() {
            return errors.isEmpty();
        }
        
        public java.util.List<String> getErrors() {
            return new java.util.ArrayList<>(errors);
        }
        
        @Override
        public String toString() {
            return isValid() ? "Valid" : "Errors: " + String.join(", ", errors);
        }
    }
    
    // Null-safe validation helpers
    public static <T> T requireNonNull(T obj, String message) {
        return Objects.requireNonNull(obj, message);
    }
    
    public static String requireNonEmptyString(String str, String fieldName) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        return str.trim();
    }
}