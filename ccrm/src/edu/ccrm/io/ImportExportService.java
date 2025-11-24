package edu.ccrm.io;

import edu.ccrm.config.AppConfig;
import edu.ccrm.domain.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * Service for import/export operations using NIO.2 and Streams.
 * Demonstrates modern I/O operations, Path API, and Stream processing.
 */
public class ImportExportService {
    private final AppConfig config;
    private final String CSV_SEPARATOR;
    
    public ImportExportService() {
        this.config = AppConfig.getInstance();
        this.CSV_SEPARATOR = config.getCsvSeparator();
    }
    
    // Create data directory if it doesn't exist
    private void ensureDataDirectoryExists() throws IOException {
        Path dataPath = config.getDataPath();
        if (!Files.exists(dataPath)) {
            Files.createDirectories(dataPath);
        }
    }
    
    // Export students to CSV
    public void exportStudents(List<Student> students) throws IOException {
        ensureDataDirectoryExists();
        Path filePath = config.getStudentsFilePath();
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, 
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            
            // Write header
            writer.write("ID,RegNo,FirstName,LastName,Email,DateOfBirth,Active,EnrollmentDate,MaxCredits");
            writer.newLine();
            
            // Write student data using streams
            students.stream()
                   .filter(Objects::nonNull)
                   .forEach(student -> {
                       try {
                           String line = String.join(CSV_SEPARATOR,
                               student.getId(),
                               student.getRegNo(),
                               student.getFullName().getFirstName(),
                               student.getFullName().getLastName(),
                               student.getEmail(),
                               student.getDateOfBirth().format(config.getDateFormatter()),
                               String.valueOf(student.isActive()),
                               student.getEnrollmentDate().format(config.getDateFormatter()),
                               String.valueOf(student.getMaxCreditsPerSemester())
                           );
                           writer.write(line);
                           writer.newLine();
                       } catch (IOException e) {
                           throw new UncheckedIOException(e);
                       }
                   });
        }
    }
    
    // Import students from CSV
    public List<Student> importStudents() throws IOException {
        Path filePath = config.getStudentsFilePath();
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.skip(1) // Skip header
                        .filter(line -> !line.trim().isEmpty())
                        .map(this::parseStudentFromCsv)
                        .filter(Objects::nonNull)
                        .toList();
        }
    }
    
    private Student parseStudentFromCsv(String line) {
        try {
            String[] fields = line.split(CSV_SEPARATOR);
            if (fields.length < 9) return null;
            
            Name name = new Name(fields[2].trim(), fields[3].trim());
            Student student = new Student(
                fields[0].trim(), // ID
                fields[1].trim(), // RegNo
                name,
                fields[4].trim(), // Email
                java.time.LocalDate.parse(fields[5].trim(), config.getDateFormatter()) // DOB
            );
            
            student.setActive(Boolean.parseBoolean(fields[6].trim()));
            student.setEnrollmentDate(java.time.LocalDate.parse(fields[7].trim(), config.getDateFormatter()));
            student.setMaxCreditsPerSemester(Integer.parseInt(fields[8].trim()));
            
            return student;
        } catch (Exception e) {
            System.err.println("Error parsing student line: " + line + " - " + e.getMessage());
            return null;
        }
    }
    
    // Export courses to CSV
    public void exportCourses(List<Course> courses) throws IOException {
        ensureDataDirectoryExists();
        Path filePath = config.getCoursesFilePath();
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            
            // Write header
            writer.write("Code,Title,Credits,InstructorId,Semester,Department,MaxCapacity,Active");
            writer.newLine();
            
            // Write course data
            courses.stream()
                   .filter(Objects::nonNull)
                   .forEach(course -> {
                       try {
                           String line = String.join(CSV_SEPARATOR,
                               course.getCode(),
                               course.getTitle(),
                               String.valueOf(course.getCredits()),
                               course.getInstructorId() != null ? course.getInstructorId() : "",
                               course.getSemester().name(),
                               course.getDepartment() != null ? course.getDepartment() : "",
                               String.valueOf(course.getMaxCapacity()),
                               String.valueOf(course.isActive())
                           );
                           writer.write(line);
                           writer.newLine();
                       } catch (IOException e) {
                           throw new UncheckedIOException(e);
                       }
                   });
        }
    }
    
    // Import courses from CSV
    public List<Course> importCourses() throws IOException {
        Path filePath = config.getCoursesFilePath();
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.skip(1) // Skip header
                        .filter(line -> !line.trim().isEmpty())
                        .map(this::parseCourseFromCsv)
                        .filter(Objects::nonNull)
                        .toList();
        }
    }
    
    private Course parseCourseFromCsv(String line) {
        try {
            String[] fields = line.split(CSV_SEPARATOR);
            if (fields.length < 8) return null;
            
            Course.Builder builder = new Course.Builder(fields[0].trim(), fields[1].trim())
                    .credits(Integer.parseInt(fields[2].trim()))
                    .semester(Semester.valueOf(fields[4].trim()))
                    .maxCapacity(Integer.parseInt(fields[6].trim()));
            
            if (!fields[3].trim().isEmpty()) {
                builder.instructor(fields[3].trim());
            }
            
            if (!fields[5].trim().isEmpty()) {
                builder.department(fields[5].trim());
            }
            
            Course course = builder.build();
            course.setActive(Boolean.parseBoolean(fields[7].trim()));
            
            return course;
        } catch (Exception e) {
            System.err.println("Error parsing course line: " + line + " - " + e.getMessage());
            return null;
        }
    }
    
    // Export enrollments to CSV
    public void exportEnrollments(List<Enrollment> enrollments) throws IOException {
        ensureDataDirectoryExists();
        Path filePath = config.getEnrollmentsFilePath();
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            
            // Write header
            writer.write("StudentId,CourseCode,EnrollmentDate,Grade,Active,Notes");
            writer.newLine();
            
            // Write enrollment data
            enrollments.stream()
                      .filter(Objects::nonNull)
                      .forEach(enrollment -> {
                          try {
                              String line = String.join(CSV_SEPARATOR,
                                  enrollment.getStudentId(),
                                  enrollment.getCourseCode(),
                                  enrollment.getEnrollmentDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                  enrollment.getGrade() != null ? enrollment.getGrade().name() : "",
                                  String.valueOf(enrollment.isActive()),
                                  enrollment.getNotes() != null ? enrollment.getNotes() : ""
                              );
                              writer.write(line);
                              writer.newLine();
                          } catch (IOException e) {
                              throw new UncheckedIOException(e);
                          }
                      });
        }
    }
    
    // Import enrollments from CSV
    public List<Enrollment> importEnrollments() throws IOException {
        Path filePath = config.getEnrollmentsFilePath();
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.skip(1) // Skip header
                        .filter(line -> !line.trim().isEmpty())
                        .map(this::parseEnrollmentFromCsv)
                        .filter(Objects::nonNull)
                        .toList();
        }
    }
    
    private Enrollment parseEnrollmentFromCsv(String line) {
        try {
            String[] fields = line.split(CSV_SEPARATOR);
            if (fields.length < 6) return null;
            
            // Parse enrollment date
            LocalDateTime enrollmentDate = java.time.LocalDateTime.parse(fields[2].trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            Enrollment enrollment = new Enrollment(
                fields[0].trim(), // StudentId
                fields[1].trim(), // CourseCode
                enrollmentDate    // EnrollmentDate
            );
            
            // Set grade if present
            if (!fields[3].trim().isEmpty()) {
                try {
                    Grade grade = Grade.valueOf(fields[3].trim());
                    enrollment.setGrade(grade);
                } catch (IllegalArgumentException e) {
                    // Ignore invalid grades
                }
            }
            
            // Set active status
            enrollment.setActive(Boolean.parseBoolean(fields[4].trim()));
            
            // Set notes if present
            if (fields.length > 5 && !fields[5].trim().isEmpty()) {
                enrollment.setNotes(fields[5].trim());
            }
            
            return enrollment;
        } catch (Exception e) {
            System.err.println("Error parsing enrollment line: " + line + " - " + e.getMessage());
            return null;
        }
    }
    
    // Check if file exists
    public boolean fileExists(Path filePath) {
        return Files.exists(filePath);
    }
    
    // Get file size
    public long getFileSize(Path filePath) throws IOException {
        return Files.size(filePath);
    }
    
    // Get last modified time
    public LocalDateTime getLastModified(Path filePath) throws IOException {
        return LocalDateTime.ofInstant(
            Files.getLastModifiedTime(filePath).toInstant(),
            java.time.ZoneId.systemDefault()
        );
    }
    
    // List all data files
    public List<Path> listDataFiles() throws IOException {
        Path dataPath = config.getDataPath();
        if (!Files.exists(dataPath)) {
            return new ArrayList<>();
        }
        
        try (Stream<Path> files = Files.list(dataPath)) {
            return files.filter(Files::isRegularFile)
                       .filter(path -> path.toString().endsWith(".csv"))
                       .toList();
        }
    }
}