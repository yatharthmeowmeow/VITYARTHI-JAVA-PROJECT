package edu.ccrm.domain;

/**
 * Enum representing different semesters with their properties.
 * Demonstrates enum with constructors and fields.
 */
public enum Semester {
    SPRING("Spring", 1),
    SUMMER("Summer", 2),
    FALL("Fall", 3);
    
    private final String displayName;
    private final int order;
    
    // Enum constructor
    Semester(String displayName, int order) {
        this.displayName = displayName;
        this.order = order;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getOrder() {
        return order;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    // Static method to get semester by order
    public static Semester getByOrder(int order) {
        for (Semester semester : values()) {
            if (semester.order == order) {
                return semester;
            }
        }
        throw new IllegalArgumentException("Invalid semester order: " + order);
    }
}