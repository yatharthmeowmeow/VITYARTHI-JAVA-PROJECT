package edu.ccrm.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

/**
 * Singleton configuration class for the CCRM application.
 * Demonstrates Singleton design pattern with thread safety.
 */
public final class AppConfig {
    // Configuration properties
    private final String dataFolderPath;
    private final String backupFolderPath;
    private final DateTimeFormatter dateFormatter;
    private final DateTimeFormatter timestampFormatter;
    private final int defaultMaxCredits;
    private final String csvSeparator;
    
    // Private constructor prevents instantiation
    private AppConfig() {
        // Initialize default values
        this.dataFolderPath = "data";
        this.backupFolderPath = "backup";
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        this.defaultMaxCredits = 24;
        this.csvSeparator = ",";
    }
    
    // Thread-safe singleton implementation using enum (best practice)
    private static enum SingletonHelper {
        INSTANCE;
        private final AppConfig appConfig = new AppConfig();
    }
    
    public static AppConfig getInstance() {
        return SingletonHelper.INSTANCE.appConfig;
    }
    
    // Prevent cloning
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Singleton cannot be cloned");
    }
    
    // Getters for configuration values
    public String getDataFolderPath() {
        return dataFolderPath;
    }
    
    public Path getDataPath() {
        return Paths.get(dataFolderPath);
    }
    
    public String getBackupFolderPath() {
        return backupFolderPath;
    }
    
    public Path getBackupPath() {
        return Paths.get(backupFolderPath);
    }
    
    public DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }
    
    public DateTimeFormatter getTimestampFormatter() {
        return timestampFormatter;
    }
    
    public int getDefaultMaxCredits() {
        return defaultMaxCredits;
    }
    
    public String getCsvSeparator() {
        return csvSeparator;
    }
    
    // File paths
    public Path getStudentsFilePath() {
        return getDataPath().resolve("students.csv");
    }
    
    public Path getCoursesFilePath() {
        return getDataPath().resolve("courses.csv");
    }
    
    public Path getEnrollmentsFilePath() {
        return getDataPath().resolve("enrollments.csv");
    }
    
    public Path getInstructorsFilePath() {
        return getDataPath().resolve("instructors.csv");
    }
    
    // Application metadata
    public String getApplicationName() {
        return "Campus Course & Records Manager (CCRM)";
    }
    
    public String getVersion() {
        return "1.0.0";
    }
    
    public String getJavaVersionInfo() {
        return String.format("Java %s (%s)", 
                           System.getProperty("java.version"),
                           System.getProperty("java.vendor"));
    }
    
    public String getPlatformInfo() {
        return String.format("Running on %s %s (%s)", 
                           System.getProperty("os.name"),
                           System.getProperty("os.version"),
                           System.getProperty("os.arch"));
    }
    
    // Load configuration from properties file (future enhancement)
    public void loadConfiguration(Properties properties) {
        // Implementation for loading external configuration
        // This demonstrates how the singleton could be extended
    }
    
    @Override
    public String toString() {
        return String.format("AppConfig{dataPath='%s', backupPath='%s', maxCredits=%d}", 
                           dataFolderPath, backupFolderPath, defaultMaxCredits);
    }
}