package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.*;

/**
 * Instructor class extending Person.
 * Demonstrates inheritance and polymorphism.
 */
public class Instructor extends Person {
    private String employeeId;
    private String department;
    private final Set<String> assignedCourses; // Course codes
    private LocalDate hireDate;
    private double salary;
    
    public Instructor(String id, String employeeId, Name fullName, String email, 
                     LocalDate dateOfBirth, String department) {
        super(id, fullName, email, dateOfBirth);
        this.employeeId = Objects.requireNonNull(employeeId, "Employee ID cannot be null");
        this.department = Objects.requireNonNull(department, "Department cannot be null");
        this.assignedCourses = new HashSet<>();
        this.hireDate = LocalDate.now();
        this.salary = 0.0;
    }
    
    @Override
    public String getRole() {
        return "Instructor";
    }
    
    @Override
    public String getDisplayInfo() {
        return String.format("Instructor: %s (%s) - %s Department, %d courses", 
                           getFullName(), employeeId, department, assignedCourses.size());
    }
    
    // Getters and setters
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = Objects.requireNonNull(employeeId, "Employee ID cannot be null");
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = Objects.requireNonNull(department, "Department cannot be null");
    }
    
    public Set<String> getAssignedCourses() {
        return new HashSet<>(assignedCourses); // Defensive copy
    }
    
    public LocalDate getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
    
    public double getSalary() {
        return salary;
    }
    
    public void setSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        this.salary = salary;
    }
    
    // Course assignment methods
    public boolean assignCourse(String courseCode) {
        Objects.requireNonNull(courseCode, "Course code cannot be null");
        return assignedCourses.add(courseCode);
    }
    
    public boolean unassignCourse(String courseCode) {
        Objects.requireNonNull(courseCode, "Course code cannot be null");
        return assignedCourses.remove(courseCode);
    }
    
    public boolean isAssignedTo(String courseCode) {
        return assignedCourses.contains(courseCode);
    }
    
    public int getCourseLoad() {
        return assignedCourses.size();
    }
    
    @Override
    public String toString() {
        return String.format("Instructor{id='%s', employeeId='%s', name='%s', dept='%s', courses=%d}", 
                           getId(), employeeId, getFullName(), department, assignedCourses.size());
    }
}