package edu.ccrm.domain;

import java.util.Objects;

/**
 * Immutable value class representing a person's name.
 * Demonstrates immutability with final fields and defensive copying.
 */
public final class Name {
    private final String firstName;
    private final String middleName;
    private final String lastName;
    
    public Name(String firstName, String lastName) {
        this(firstName, null, lastName);
    }
    
    public Name(String firstName, String middleName, String lastName) {
        this.firstName = Objects.requireNonNull(firstName, "First name cannot be null").trim();
        this.middleName = middleName != null ? middleName.trim() : null;
        this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null").trim();
        
        // Validation
        if (this.firstName.isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (this.lastName.isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getFullName() {
        if (middleName != null && !middleName.isEmpty()) {
            return firstName + " " + middleName + " " + lastName;
        }
        return firstName + " " + lastName;
    }
    
    public String getInitials() {
        StringBuilder initials = new StringBuilder();
        initials.append(firstName.charAt(0));
        if (middleName != null && !middleName.isEmpty()) {
            initials.append(middleName.charAt(0));
        }
        initials.append(lastName.charAt(0));
        return initials.toString().toUpperCase();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Name name = (Name) obj;
        return Objects.equals(firstName, name.firstName) &&
               Objects.equals(middleName, name.middleName) &&
               Objects.equals(lastName, name.lastName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(firstName, middleName, lastName);
    }
    
    @Override
    public String toString() {
        return getFullName();
    }
}