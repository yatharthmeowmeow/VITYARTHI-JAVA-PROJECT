package edu.ccrm.domain;

/**
 * Custom exception for maximum credit limit exceeded scenarios.
 * Demonstrates custom unchecked exceptions.
 */
public class MaxCreditLimitExceededException extends RuntimeException {
    private final String studentId;
    private final int currentCredits;
    private final int maxCredits;
    private final int attemptedCredits;
    
    public MaxCreditLimitExceededException(String studentId, int currentCredits, 
                                         int maxCredits, int attemptedCredits) {
        super(String.format("Student %s cannot enroll: current=%d, max=%d, attempted=%d", 
                          studentId, currentCredits, maxCredits, attemptedCredits));
        this.studentId = studentId;
        this.currentCredits = currentCredits;
        this.maxCredits = maxCredits;
        this.attemptedCredits = attemptedCredits;
    }
    
    public MaxCreditLimitExceededException(String studentId, int currentCredits, 
                                         int maxCredits, int attemptedCredits, String message) {
        super(message);
        this.studentId = studentId;
        this.currentCredits = currentCredits;
        this.maxCredits = maxCredits;
        this.attemptedCredits = attemptedCredits;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public int getCurrentCredits() {
        return currentCredits;
    }
    
    public int getMaxCredits() {
        return maxCredits;
    }
    
    public int getAttemptedCredits() {
        return attemptedCredits;
    }
    
    public int getExceededBy() {
        return (currentCredits + attemptedCredits) - maxCredits;
    }
}