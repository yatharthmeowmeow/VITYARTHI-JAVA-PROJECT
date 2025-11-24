package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstract base class representing a person in the campus system.
 * Demonstrates abstraction, inheritance, and encapsulation.
 */
public abstract class Person {
    protected String id;
    protected Name fullName; // Immutable value object
    protected String email;
    protected LocalDate dateOfBirth;
    protected boolean active;
    
    // Protected constructor for inheritance
    protected Person(String id, Name fullName, String email, LocalDate dateOfBirth) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.fullName = Objects.requireNonNull(fullName, "Full name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.dateOfBirth = Objects.requireNonNull(dateOfBirth, "Date of birth cannot be null");
        this.active = true;
    }
    
    // Abstract methods to be implemented by subclasses
    public abstract String getRole();
    public abstract String getDisplayInfo();
    
    // Getters and setters (Encapsulation)
    public String getId() {
        return id;
    }
    
    public Name getFullName() {
        return fullName;
    }
    
    public void setFullName(Name fullName) {
        this.fullName = Objects.requireNonNull(fullName, "Full name cannot be null");
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "Email cannot be null");
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = Objects.requireNonNull(dateOfBirth, "Date of birth cannot be null");
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void deactivate() {
        this.active = false;
    }
    
    public void activate() {
        this.active = true;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return Objects.equals(id, person.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("%s{id='%s', name='%s', email='%s', active=%s}", 
                            getClass().getSimpleName(), id, fullName, email, active);
    }
}