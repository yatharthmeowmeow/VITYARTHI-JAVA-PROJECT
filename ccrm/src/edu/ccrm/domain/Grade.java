package edu.ccrm.domain;

/**
 * Enum representing different grades with their grade points.
 * Demonstrates enum with constructors, fields, and methods.
 */
public enum Grade {
    S(10.0, "Outstanding"),
    A(9.0, "Excellent"),
    B(8.0, "Very Good"),
    C(7.0, "Good"),
    D(6.0, "Average"),
    E(5.0, "Below Average"),
    F(0.0, "Fail");
    
    private final double gradePoint;
    private final String description;
    
    Grade(double gradePoint, String description) {
        this.gradePoint = gradePoint;
        this.description = description;
    }
    
    public double getGradePoint() {
        return gradePoint;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return name() + " (" + gradePoint + ")";
    }
    
    // Static method to get grade by points range
    public static Grade getGradeByPercentage(double percentage) {
        if (percentage >= 90) return S;
        else if (percentage >= 80) return A;
        else if (percentage >= 70) return B;
        else if (percentage >= 60) return C;
        else if (percentage >= 50) return D;
        else if (percentage >= 40) return E;
        else return F;
    }
    
    public boolean isPassing() {
        return this != F;
    }
}