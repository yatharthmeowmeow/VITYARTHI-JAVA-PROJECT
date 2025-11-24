package edu.ccrm.cli;

import edu.ccrm.config.AppConfig;
import edu.ccrm.domain.*;
import edu.ccrm.io.*;
import edu.ccrm.service.*;
import edu.ccrm.util.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Main CLI application class with menu-driven interface.
 * Demonstrates switch statements, loops, exception handling, and polymorphism.
 */
public class CCRMApplication {
    private final Scanner scanner;
    private final AppConfig config;
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final ImportExportService importExportService;
    private final BackupService backupService;
    
    // Anonymous inner class for input validation
    private final InputValidator inputValidator = new InputValidator() {
        @Override
        public boolean isValidInput(String input) {
            return input != null && !input.trim().isEmpty();
        }
        
        @Override
        public String sanitizeInput(String input) {
            return input != null ? input.trim() : "";
        }
    };
    
    public CCRMApplication() {
        this.scanner = new Scanner(System.in);
        this.config = AppConfig.getInstance();
        this.studentService = new StudentService();
        this.courseService = new CourseService();
        this.enrollmentService = new EnrollmentService(studentService, courseService);
        this.importExportService = new ImportExportService();
        this.backupService = new BackupService();
        
        // Load initial data
        loadInitialData();
        
        // Add shutdown hook to save data when application exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("\nSaving data before exit...");
                autoSaveData();
                System.out.println("Data saved successfully.");
            } catch (Exception e) {
                System.err.println("Error saving data on exit: " + e.getMessage());
            }
        }));
    }
    
    // Anonymous inner class interface
    private interface InputValidator {
        boolean isValidInput(String input);
        String sanitizeInput(String input);
    }
    
    private void loadInitialData() {
        try {
            // Try to import existing data
            List<Student> students = importExportService.importStudents();
            for (Student student : students) {
                try {
                    studentService.save(student);
                } catch (Exception e) {
                    System.err.println("Error loading student: " + e.getMessage());
                }
            }
            
            List<Course> courses = importExportService.importCourses();
            for (Course course : courses) {
                try {
                    courseService.save(course);
                } catch (Exception e) {
                    System.err.println("Error loading course: " + e.getMessage());
                }
            }
            
            // Load enrollments after students and courses are loaded
            List<Enrollment> enrollments = importExportService.importEnrollments();
            for (Enrollment enrollment : enrollments) {
                try {
                    // Re-enroll students in courses and restore grades
                    enrollmentService.restoreEnrollment(enrollment);
                } catch (Exception e) {
                    System.err.println("Error loading enrollment: " + e.getMessage());
                }
            }
            
            System.out.println("Loaded " + students.size() + " students, " + courses.size() + " courses, and " + enrollments.size() + " enrollments.");
            
        } catch (Exception e) {
            System.out.println("No existing data found. Starting with empty database.");
        }
    }
    
    // Auto-save data to persist changes
    private void autoSaveData() {
        try {
            importExportService.exportStudents(studentService.findAll());
            importExportService.exportCourses(courseService.findAll());
            importExportService.exportEnrollments(enrollmentService.getActiveEnrollments());
        } catch (Exception e) {
            System.err.println("Auto-save failed: " + e.getMessage());
        }
    }
    

    
    public void run() {
        printWelcomeMessage();
        printPlatformInfo();
        
        boolean running = true;
        
        // Main application loop
        while (running) {
            try {
                printMainMenu();
                int choice = getIntInput("Enter your choice: ");
                
                // Enhanced switch expression (Java 14+) demonstration
                running = switch (choice) {
                    case 1 -> { manageStudents(); yield true; }
                    case 2 -> { manageCourses(); yield true; }
                    case 3 -> { manageEnrollments(); yield true; }
                    case 4 -> { manageGrades(); yield true; }
                    case 5 -> { generateReports(); yield true; }
                    case 6 -> { fileOperations(); yield true; }
                    case 7 -> { backupOperations(); yield true; }
                    case 8 -> { showSystemInfo(); yield true; }
                    case 0 -> { 
                        System.out.println("Thank you for using CCRM!"); 
                        yield false; 
                    }
                    default -> {
                        System.out.println("Invalid choice. Please try again.");
                        yield true;
                    }
                };
                
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }
        
        scanner.close();
    }
    
    private void printWelcomeMessage() {
        System.out.println("=" + "=".repeat(60) + "=");
        System.out.println("    " + config.getApplicationName());
        System.out.println("    Version " + config.getVersion());
        System.out.println("=" + "=".repeat(60) + "=");
    }
    
    private void printPlatformInfo() {
        System.out.println("\n--- Platform Information ---");
        System.out.println("Java Version: " + config.getJavaVersionInfo());
        System.out.println("Platform: " + config.getPlatformInfo());
        System.out.println("\n--- Java Platform Editions ---");
        System.out.println("• Java SE (Standard Edition): Desktop and server applications");
        System.out.println("• Java EE (Enterprise Edition): Large-scale enterprise applications");  
        System.out.println("• Java ME (Micro Edition): Mobile and embedded devices");
        System.out.println("This application is built with Java SE.\n");
    }
    
    private void printMainMenu() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("MAIN MENU");
        System.out.println("─".repeat(50));
        System.out.println("1. Manage Students");
        System.out.println("2. Manage Courses");  
        System.out.println("3. Manage Enrollments");
        System.out.println("4. Manage Grades");
        System.out.println("5. Generate Reports");
        System.out.println("6. File Operations");
        System.out.println("7. Backup Operations");
        System.out.println("8. System Information");
        System.out.println("0. Exit");
        System.out.println("─".repeat(50));
    }
    
    private void manageStudents() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- Student Management ---");
            System.out.println("1. Add Student");
            System.out.println("2. List All Students");
            System.out.println("3. Search Students");
            System.out.println("4. Update Student");
            System.out.println("5. Deactivate Student");
            System.out.println("6. Show Student Profile");
            System.out.println("0. Back to Main Menu");
            
            int choice = getIntInput("Enter choice: ");
            
            switch (choice) {
                case 1: addStudent(); break;
                case 2: listAllStudents(); break;
                case 3: searchStudents(); break;
                case 4: updateStudent(); break;
                case 5: deactivateStudent(); break;
                case 6: showStudentProfile(); break;
                case 0: back = true; break;
                default: System.out.println("Invalid choice!");
            }
        }
    }
    
    private void addStudent() {
        try {
            System.out.println("\n--- Add New Student ---");
            
            String id = getStringInput("Enter Student ID: ");
            String regNo = getStringInput("Enter Registration Number: ");
            String firstName = getStringInput("Enter First Name: ");
            String lastName = getStringInput("Enter Last Name: ");
            String email = getStringInput("Enter Email: ");
            
            // Validate inputs
            Validators.ValidationResult validation = Validators.validateStudent(
                id, regNo, firstName, lastName, email, LocalDate.now().minusYears(20));
            
            if (!validation.isValid()) {
                System.out.println("Validation errors:");
                validation.getErrors().forEach(error -> System.out.println("• " + error));
                return;
            }
            
            Name fullName = new Name(firstName, lastName);
            LocalDate dateOfBirth = getDateInput("Enter Date of Birth (YYYY-MM-DD): ");
            
            Student student = new Student(id, regNo, fullName, email, dateOfBirth);
            studentService.save(student);
            autoSaveData(); // Auto-save after adding student
            
            System.out.println("Student added successfully!");
            
        } catch (Exception e) {
            System.err.println("Error adding student: " + e.getMessage());
        }
    }
    
    private void listAllStudents() {
        List<Student> students = studentService.findAll();
        
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        
        System.out.println("\n--- All Students ---");
        System.out.printf("%-10s %-12s %-25s %-25s %-8s %-8s%n", 
                         "ID", "Reg No", "Name", "Email", "Courses", "GPA");
        System.out.println("─".repeat(90));
        
        // Sort students by name using comparator
        students.stream()
               .sorted(Comparators.BY_NAME)
               .forEach(student -> {
                   System.out.printf("%-10s %-12s %-25s %-25s %-8d %-8.2f%n",
                                   student.getId(),
                                   student.getRegNo(),
                                   student.getFullName().getFullName(),
                                   student.getEmail(),
                                   student.getEnrolledCourses().size(),
                                   student.calculateGPA());
               });
    }
    
    private void searchStudents() {
        String query = getStringInput("Enter search term (name, reg no, or email): ");
        List<Student> results = studentService.search(query);
        
        if (results.isEmpty()) {
            System.out.println("No students found matching: " + query);
            return;
        }
        
        System.out.println("\n--- Search Results ---");
        results.forEach(student -> System.out.println(student.getDisplayInfo()));
    }
    
    private void updateStudent() {
        String id = getStringInput("Enter Student ID to update: ");
        Optional<Student> studentOpt = studentService.findById(id);
        
        if (studentOpt.isEmpty()) {
            System.out.println("Student not found!");
            return;
        }
        
        Student student = studentOpt.get();
        System.out.println("Current details: " + student);
        
        // Update fields (simplified - in real app, show current values)
        String newEmail = getStringInput("Enter new email (or press Enter to skip): ");
        if (!newEmail.isEmpty() && Validators.isValidEmail(newEmail)) {
            student.setEmail(newEmail);
        }
        
        try {
            studentService.updateStudent(id, student);
            autoSaveData(); // Auto-save after updating student
            System.out.println("Student updated successfully!");
        } catch (Exception e) {
            System.err.println("Error updating student: " + e.getMessage());
        }
    }
    
    private void deactivateStudent() {
        String id = getStringInput("Enter Student ID to deactivate: ");
        studentService.deactivateStudent(id);
        autoSaveData(); // Auto-save after deactivating student
        System.out.println("Student deactivated successfully!");
    }
    
    private void showStudentProfile() {
        String id = getStringInput("Enter Student ID: ");
        Optional<Student> studentOpt = studentService.findById(id);
        
        if (studentOpt.isEmpty()) {
            System.out.println("Student not found!");
            return;
        }
        
        Student student = studentOpt.get();
        System.out.println("\n" + "=".repeat(50));
        System.out.println("STUDENT PROFILE");
        System.out.println("=".repeat(50));
        System.out.println("ID: " + student.getId());
        System.out.println("Registration No: " + student.getRegNo());
        System.out.println("Name: " + student.getFullName().getFullName());
        System.out.println("Email: " + student.getEmail());
        System.out.println("Date of Birth: " + student.getDateOfBirth());
        System.out.println("Enrollment Date: " + student.getEnrollmentDate());
        System.out.println("Status: " + (student.isActive() ? "Active" : "Inactive"));
        System.out.println("Current GPA: " + String.format("%.2f", student.calculateGPA()));
        System.out.println("Enrolled Courses: " + student.getEnrolledCourses().size());
        
        if (!student.getEnrolledCourses().isEmpty()) {
            System.out.println("\nCourse Details:");
            student.getEnrolledCourses().forEach(courseCode -> {
                Optional<Course> courseOpt = courseService.findById(courseCode);
                Grade grade = student.getGrade(courseCode);
                System.out.printf("  • %s: %s (Grade: %s)%n", 
                                courseCode, 
                                courseOpt.map(Course::getTitle).orElse("N/A"),
                                grade != null ? grade : "Not graded");
            });
        }
    }
    
    // Course management methods (simplified for brevity)
    private void manageCourses() {
        System.out.println("\n--- Course Management ---");
        System.out.println("1. Add Course");
        System.out.println("2. List Courses");
        System.out.println("3. Search Courses");
        System.out.println("0. Back");
        
        int choice = getIntInput("Enter choice: ");
        switch (choice) {
            case 1: addCourse(); break;
            case 2: listCourses(); break;
            case 3: searchCourses(); break;
        }
    }
    
    private void addCourse() {
        try {
            System.out.println("\n--- Add New Course ---");
            
            String code = getStringInput("Enter Course Code: ");
            String title = getStringInput("Enter Course Title: ");
            int credits = getIntInput("Enter Credits: ");
            String department = getStringInput("Enter Department: ");
            
            Course course = new Course.Builder(code, title)
                    .credits(credits)
                    .department(department)
                    .semester(Semester.FALL)
                    .build();
                    
            courseService.save(course);
            autoSaveData(); // Auto-save after adding course
            System.out.println("Course added successfully!");
            
        } catch (Exception e) {
            System.err.println("Error adding course: " + e.getMessage());
        }
    }
    
    private void listCourses() {
        List<Course> courses = courseService.findAll();
        
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }
        
        System.out.println("\n--- All Courses ---");
        courses.stream()
               .sorted(Comparators.BY_CODE)
               .forEach(System.out::println);
    }
    
    private void searchCourses() {
        String query = getStringInput("Enter search term: ");
        List<Course> results = courseService.search(query);
        
        if (results.isEmpty()) {
            System.out.println("No courses found matching: " + query);
            return;
        }
        
        System.out.println("\n--- Search Results ---");
        results.forEach(System.out::println);
    }
    
    // Continue with other menu methods...
    private void manageEnrollments() {
        System.out.println("\n--- Enrollment Management ---");
        System.out.println("1. Enroll Student in Course");
        System.out.println("2. Unenroll Student from Course");
        System.out.println("3. List Student Enrollments");
        System.out.println("0. Back");
        
        int choice = getIntInput("Enter choice: ");
        switch (choice) {
            case 1: enrollStudentInCourse(); break;
            case 2: unenrollStudentFromCourse(); break;
            case 3: listStudentEnrollments(); break;
        }
    }
    
    private void enrollStudentInCourse() {
        try {
            String studentId = getStringInput("Enter Student ID: ");
            String courseCode = getStringInput("Enter Course Code: ");
            
            enrollmentService.enrollStudent(studentId, courseCode);
            autoSaveData(); // Auto-save after enrollment
            System.out.println("Student enrolled successfully!");
            
        } catch (DuplicateEnrollmentException e) {
            System.err.println("Enrollment Error: " + e.getMessage());
        } catch (MaxCreditLimitExceededException e) {
            System.err.println("Credit Limit Error: " + e.getMessage());
            System.err.println("Exceeded by: " + e.getExceededBy() + " credits");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    private void unenrollStudentFromCourse() {
        String studentId = getStringInput("Enter Student ID: ");
        String courseCode = getStringInput("Enter Course Code: ");
        
        boolean success = enrollmentService.unenrollStudent(studentId, courseCode);
        
        if (success) {
            autoSaveData(); // Auto-save after unenrollment
            System.out.println("Student unenrolled successfully!");
        } else {
            System.out.println("Enrollment not found!");
        }
    }
    
    private void listStudentEnrollments() {
        String studentId = getStringInput("Enter Student ID: ");
        List<Enrollment> enrollments = enrollmentService.getStudentEnrollments(studentId);
        
        if (enrollments.isEmpty()) {
            System.out.println("No enrollments found for student: " + studentId);
            return;
        }
        
        System.out.println("\n--- Student Enrollments ---");
        enrollments.forEach(System.out::println);
    }
    
    private void manageGrades() {
        System.out.println("\n--- Grade Management ---");
        System.out.println("1. Record Grade");
        System.out.println("2. View Student Transcript");
        System.out.println("0. Back");
        
        int choice = getIntInput("Enter choice: ");
        switch (choice) {
            case 1: recordGrade(); break;
            case 2: viewTranscript(); break;
        }
    }
    
    private void recordGrade() {
        try {
            String studentId = getStringInput("Enter Student ID: ");
            String courseCode = getStringInput("Enter Course Code: ");
            
            System.out.println("Available Grades:");
            for (Grade grade : Grade.values()) {
                System.out.println(grade.name() + " - " + grade.getDescription());
            }
            
            String gradeStr = getStringInput("Enter Grade: ").toUpperCase();
            System.out.println("DEBUG: Entered grade string: '" + gradeStr + "'");
            
            Grade grade = Grade.valueOf(gradeStr);
            System.out.println("DEBUG: Parsed grade: " + grade);
            
            enrollmentService.recordGrade(studentId, courseCode, grade);
            autoSaveData(); // Auto-save after recording grade
            System.out.println("Grade recorded successfully!");
            
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Please enter a valid grade (S, A, B, C, D, E, or F)");
        } catch (Exception e) {
            System.err.println("Error recording grade: " + e.getMessage());
        }
    }
    
    private void viewTranscript() {
        String studentId = getStringInput("Enter Student ID: ");
        Optional<Student> studentOpt = studentService.findById(studentId);
        
        if (studentOpt.isEmpty()) {
            System.out.println("Student not found!");
            return;
        }
        
        Student student = studentOpt.get();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ACADEMIC TRANSCRIPT");
        System.out.println("=".repeat(60));
        System.out.println("Student: " + student.getFullName().getFullName());
        System.out.println("Registration No: " + student.getRegNo());
        System.out.println("=".repeat(60));
        
        Map<String, Grade> grades = student.getGrades();
        if (grades.isEmpty()) {
            System.out.println("No grades recorded.");
            return;
        }
        
        System.out.printf("%-12s %-30s %-8s %-12s%n", "Course", "Title", "Credits", "Grade");
        System.out.println("─".repeat(60));
        
        for (Map.Entry<String, Grade> entry : grades.entrySet()) {
            String courseCode = entry.getKey();
            Grade grade = entry.getValue();
            
            Optional<Course> courseOpt = courseService.findById(courseCode);
            String title = courseOpt.map(Course::getTitle).orElse("N/A");
            int credits = courseOpt.map(Course::getCredits).orElse(3);
            
            System.out.printf("%-12s %-30s %-8d %-12s%n", 
                            courseCode, title, credits, grade);
        }
        
        System.out.println("─".repeat(60));
        System.out.printf("Overall GPA: %.2f%n", student.calculateGPA());
    }
    
    // Continue in next part due to length...
    private void generateReports() {
        System.out.println("\n--- Reports ---");
        System.out.println("1. Student Statistics");
        System.out.println("2. Course Statistics");
        System.out.println("3. Top Students by GPA");
        System.out.println("4. Grade Distribution");
        System.out.println("0. Back");
        
        int choice = getIntInput("Enter choice: ");
        switch (choice) {
            case 1: showStudentStatistics(); break;
            case 2: showCourseStatistics(); break;
            case 3: showTopStudents(); break;
            case 4: showGradeDistribution(); break;
        }
    }
    
    private void showStudentStatistics() {
        System.out.println("\n--- Student Statistics ---");
        System.out.println("Total Students: " + studentService.count());
        System.out.println("Active Students: " + studentService.countActiveStudents());
        System.out.println("Average GPA: " + String.format("%.2f", studentService.getAverageGPA()));
        
        Map<Integer, Long> distribution = studentService.getEnrollmentDistribution();
        System.out.println("\nEnrollment Distribution:");
        distribution.entrySet().stream()
                   .sorted(Map.Entry.comparingByKey())
                   .forEach(entry -> 
                       System.out.println("  " + entry.getKey() + " courses: " + entry.getValue() + " students"));
    }
    
    private void showCourseStatistics() {
        System.out.println("\n--- Course Statistics ---");
        System.out.println("Total Courses: " + courseService.count());
        System.out.println("Average Enrollment: " + String.format("%.1f", courseService.getAverageEnrollment()));
        
        Map<String, Long> deptDistribution = courseService.getDepartmentDistribution();
        System.out.println("\nDepartment Distribution:");
        deptDistribution.entrySet().stream()
                       .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                       .forEach(entry -> 
                           System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " courses"));
    }
    
    private void showTopStudents() {
        int limit = getIntInput("Enter number of top students to show: ");
        List<Student> topStudents = studentService.findTopStudentsByGPA(limit);
        
        System.out.println("\n--- Top " + limit + " Students by GPA ---");
        for (int i = 0; i < topStudents.size(); i++) {
            Student student = topStudents.get(i);
            System.out.printf("%d. %s (Reg: %s) - GPA: %.2f%n", 
                            i + 1, 
                            student.getFullName().getFullName(),
                            student.getRegNo(),
                            student.calculateGPA());
        }
    }
    
    private void showGradeDistribution() {
        Map<Grade, Long> distribution = enrollmentService.getGradeDistribution();
        
        System.out.println("\n--- Grade Distribution ---");
        for (Grade grade : Grade.values()) {
            long count = distribution.getOrDefault(grade, 0L);
            System.out.printf("%s: %d (%.1f%%)%n", 
                            grade, count, 
                            enrollmentService.getTotalEnrollments() > 0 ? 
                                (count * 100.0 / enrollmentService.getTotalEnrollments()) : 0.0);
        }
        
        System.out.printf("\nPassing Rate: %.1f%%%n", enrollmentService.getPassingRate());
    }
    
    private void fileOperations() {
        System.out.println("\n--- File Operations ---");
        System.out.println("1. Export Data");
        System.out.println("2. Import Data");
        System.out.println("3. List Data Files");
        System.out.println("0. Back");
        
        int choice = getIntInput("Enter choice: ");
        switch (choice) {
            case 1: exportData(); break;
            case 2: importData(); break;
            case 3: listDataFiles(); break;
        }
    }
    
    private void exportData() {
        try {
            System.out.println("Exporting data...");
            
            importExportService.exportStudents(studentService.findAll());
            importExportService.exportCourses(courseService.findAll());
            importExportService.exportEnrollments(enrollmentService.getActiveEnrollments());
            
            System.out.println("Data exported successfully!");
            
        } catch (Exception e) {
            System.err.println("Error exporting data: " + e.getMessage());
        }
    }
    
    private void importData() {
        try {
            System.out.println("Importing data...");
            
            List<Student> students = importExportService.importStudents();
            List<Course> courses = importExportService.importCourses();
            
            // Clear existing data
            studentService.deleteAll();
            courseService.deleteAll();
            
            // Import new data
            for (Student student : students) {
                studentService.save(student);
            }
            
            for (Course course : courses) {
                courseService.save(course);
            }
            
            System.out.println("Imported " + students.size() + " students and " + courses.size() + " courses.");
            
        } catch (Exception e) {
            System.err.println("Error importing data: " + e.getMessage());
        }
    }
    
    private void listDataFiles() {
        try {
            System.out.println("\n--- Data Files ---");
            importExportService.listDataFiles().forEach(path -> {
                try {
                    long size = importExportService.getFileSize(path);
                    System.out.printf("%-20s %s%n", 
                                    path.getFileName(), 
                                    BackupService.formatFileSize(size));
                } catch (Exception e) {
                    System.out.println(path.getFileName() + " (error reading size)");
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error listing files: " + e.getMessage());
        }
    }
    
    private void backupOperations() {
        System.out.println("\n--- Backup Operations ---");
        System.out.println("1. Create Backup");
        System.out.println("2. List Backups");
        System.out.println("3. Show Backup Size (Recursive)");
        System.out.println("4. Clean Old Backups");
        System.out.println("0. Back");
        
        int choice = getIntInput("Enter choice: ");
        switch (choice) {
            case 1: createBackup(); break;
            case 2: listBackups(); break;
            case 3: showBackupSize(); break;
            case 4: cleanBackups(); break;
        }
    }
    
    private void createBackup() {
        try {
            System.out.println("Creating backup...");
            
            // First export current data
            exportData();
            
            // Then create backup
            java.nio.file.Path backupPath = backupService.createBackup();
            
            System.out.println("Backup created: " + backupPath.getFileName());
            
            // Show backup info
            BackupService.BackupInfo info = backupService.getBackupInfo(backupPath);
            if (info != null) {
                System.out.println("Backup size: " + info.getFormattedSize());
                System.out.println("Files: " + info.getFileCount());
            }
            
        } catch (Exception e) {
            System.err.println("Error creating backup: " + e.getMessage());
        }
    }
    
    private void listBackups() {
        try {
            List<java.nio.file.Path> backups = backupService.listBackupFolders();
            
            if (backups.isEmpty()) {
                System.out.println("No backups found.");
                return;
            }
            
            System.out.println("\n--- Available Backups ---");
            for (java.nio.file.Path backup : backups) {
                BackupService.BackupInfo info = backupService.getBackupInfo(backup);
                if (info != null) {
                    System.out.println(info);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error listing backups: " + e.getMessage());
        }
    }
    
    private void showBackupSize() {
        try {
            List<java.nio.file.Path> backups = backupService.listBackupFolders();
            
            if (backups.isEmpty()) {
                System.out.println("No backups found.");
                return;
            }
            
            System.out.println("\n--- Backup Sizes (Calculated Recursively) ---");
            
            long totalSize = 0;
            for (java.nio.file.Path backup : backups) {
                long size = backupService.calculateBackupSize(backup);
                totalSize += size;
                
                System.out.printf("%-25s %s%n", 
                                backup.getFileName(),
                                BackupService.formatFileSize(size));
                                
                // Demonstrate recursive file listing
                System.out.println("  Contents (max depth 2):");
                backupService.printFilesByDepth(backup, 2);
            }
            
            System.out.println("─".repeat(40));
            System.out.printf("Total Backup Size: %s%n", BackupService.formatFileSize(totalSize));
            
        } catch (Exception e) {
            System.err.println("Error calculating backup sizes: " + e.getMessage());
        }
    }
    
    private void cleanBackups() {
        try {
            int keepCount = getIntInput("Enter number of backups to keep: ");
            int removed = backupService.cleanOldBackups(keepCount);
            
            System.out.println("Removed " + removed + " old backup(s).");
            
        } catch (Exception e) {
            System.err.println("Error cleaning backups: " + e.getMessage());
        }
    }
    
    private void showSystemInfo() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SYSTEM INFORMATION");
        System.out.println("=".repeat(60));
        System.out.println("Application: " + config.getApplicationName());
        System.out.println("Version: " + config.getVersion());
        System.out.println("Java: " + config.getJavaVersionInfo());
        System.out.println("Platform: " + config.getPlatformInfo());
        System.out.println();
        System.out.println("Data Directory: " + config.getDataFolderPath());
        System.out.println("Backup Directory: " + config.getBackupFolderPath());
        System.out.println();
        System.out.println("Statistics:");
        System.out.println("  Students: " + studentService.count());
        System.out.println("  Courses: " + courseService.count());
        System.out.println("  Enrollments: " + enrollmentService.getTotalEnrollments());
        
        // Runtime information
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        System.out.println();
        System.out.println("Memory Usage:");
        System.out.println("  Total: " + BackupService.formatFileSize(totalMemory));
        System.out.println("  Used: " + BackupService.formatFileSize(usedMemory));
        System.out.println("  Free: " + BackupService.formatFileSize(freeMemory));
    }
    
    // Utility methods for input handling
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        return inputValidator.sanitizeInput(input);
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    private LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                String input = getStringInput(prompt);
                return LocalDate.parse(input, config.getDateFormatter());
            } catch (Exception e) {
                System.out.println("Please enter date in YYYY-MM-DD format.");
            }
        }
    }
    
    // Main method
    public static void main(String[] args) {
        // Enable assertions (demonstrate assertion usage)
        // Run with: java -ea edu.ccrm.cli.CCRMApplication
        
        System.out.println("Note: Run with '-ea' flag to enable assertions");
        
        CCRMApplication app = new CCRMApplication();
        app.run();
    }
}