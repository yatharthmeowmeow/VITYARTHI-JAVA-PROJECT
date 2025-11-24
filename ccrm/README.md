# Campus Course & Records Manager (CCRM)

A comprehensive Java SE console application for managing campus academic records, demonstrating advanced Java concepts including OOP principles, design patterns, modern I/O operations, and functional programming.

## Table of Contents
- [Project Overview](#project-overview)
- [Java Evolution](#java-evolution)
- [Java Platform Editions](#java-platform-editions)
- [Java Architecture](#java-architecture)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [Technical Implementation](#technical-implementation)
- [Features](#features)
- [Usage Examples](#usage-examples)
- [Testing](#testing)
- [Acknowledgments](#acknowledgments)

## Project Overview

CCRM is a menu-driven console application that provides comprehensive management of:
- **Students**: Registration, enrollment tracking, GPA calculation
- **Courses**: Course management with instructor assignment
- **Enrollments**: Business rule validation, credit limits
- **Grades & Transcripts**: Grade recording and transcript generation
- **File Operations**: CSV import/export with NIO.2
- **Backup System**: Automated backup with recursive file operations

### Key Features
- Object-oriented design with inheritance hierarchy
- Advanced exception handling with custom exceptions
- Modern Java I/O with NIO.2 and Streams
- Functional programming with lambdas and method references
- Design patterns (Singleton, Builder)
- Comprehensive input validation
- Statistical reporting with Stream operations

## Java Evolution

### Major Java Releases Timeline
- **1995**: Java 1.0 - Initial release with basic language features
- **1997**: Java 1.1 - Inner classes, JavaBeans, JDBC
- **1998**: Java 1.2 (J2SE) - Collections Framework, Swing GUI
- **2000**: Java 1.3 - HotSpot JVM, JNDI
- **2002**: Java 1.4 - Assert keyword, NIO, XML support
- **2004**: Java 5 (1.5) - Generics, annotations, autoboxing, enhanced for-loop
- **2006**: Java 6 - Scripting support, JDBC 4.0
- **2011**: Java 7 - Diamond operator, try-with-resources, NIO.2
- **2014**: Java 8 - Lambda expressions, Stream API, Optional
- **2017**: Java 9 - Modules (Project Jigsaw), JShell
- **2018**: Java 10 - Local variable type inference (var)
- **2018**: Java 11 (LTS) - HTTP Client, String methods
- **2019**: Java 12 - Switch expressions (preview)
- **2020**: Java 14 - Pattern matching, Records (preview)
- **2021**: Java 17 (LTS) - Sealed classes, Pattern matching for switch
- **2023**: Java 21 (LTS) - Virtual threads, Pattern matching

## Java Platform Editions

| Edition | Full Name | Target Applications | Key Components |
|---------|-----------|-------------------|----------------|
| **Java SE** | Standard Edition | Desktop, server applications, development tools | Core libraries, JVM, development tools |
| **Java EE** | Enterprise Edition | Large-scale enterprise applications | Servlets, JSP, EJB, JPA, CDI |
| **Java ME** | Micro Edition | Mobile devices, embedded systems | Reduced API set, memory optimization |

### Edition Relationships
- **Java ME** ⊂ **Java SE** ⊂ **Java EE**
- Java SE provides the foundation for all Java applications
- Java EE extends SE with enterprise-specific APIs
- Java ME is a subset optimized for resource-constrained devices

## Java Architecture

### JDK, JRE, and JVM Relationship

```
┌─────────────────────────────────┐
│            JDK                  │
│  (Java Development Kit)         │
│  ┌─────────────────────────────┐│
│  │           JRE               ││
│  │  (Java Runtime Environment) ││
│  │  ┌─────────────────────────┐││
│  │  │         JVM             │││
│  │  │ (Java Virtual Machine)  │││
│  │  │                         │││
│  │  └─────────────────────────┘││
│  │  + Core Libraries           ││
│  │  + Standard APIs            ││
│  └─────────────────────────────┘│
│  + Development Tools            │
│  + Compiler (javac)             │
│  + Debugger (jdb)               │
└─────────────────────────────────┘
```

### Component Descriptions

#### JVM (Java Virtual Machine)
- **Purpose**: Executes Java bytecode
- **Key Features**: Platform independence, memory management, garbage collection
- **Components**: Class loader, execution engine, memory areas (heap, stack, method area)

#### JRE (Java Runtime Environment)
- **Purpose**: Runtime environment for Java applications
- **Includes**: JVM + standard library + supporting files
- **Use Case**: Running Java applications (end users)

#### JDK (Java Development Kit)
- **Purpose**: Complete development environment
- **Includes**: JRE + development tools + documentation
- **Use Case**: Developing Java applications (developers)

## Installation & Setup

### Prerequisites
- Operating System: Windows 10/11, macOS 10.12+, or Linux
- Memory: Minimum 512MB RAM (2GB recommended)
- Storage: 200MB available space

### Java Installation on Windows

1. **Download JDK**
   - Visit [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Download the Windows x64 installer

2. **Install JDK**
   - Run the downloaded installer
   - Follow installation wizard
   - Note the installation path (e.g., `C:\\Program Files\\Java\\jdk-17`)

3. **Set Environment Variables**
   ```cmd
   # Set JAVA_HOME
   setx JAVA_HOME "C:\\Program Files\\Java\\jdk-17"
   
   # Add to PATH
   setx PATH "%PATH%;%JAVA_HOME%\\bin"
   ```

4. **Verify Installation**
   ```cmd
   java -version
   javac -version
   ```

### IDE Setup (Eclipse)

1. **Download Eclipse**
   - Visit [Eclipse IDE](https://www.eclipse.org/downloads/)
   - Download Eclipse IDE for Java Developers

2. **Create New Project**
   - File → New → Java Project
   - Project name: "CCRM"
   - Use default JRE
   - Create separate folders for sources and class files

3. **Import Source Code**
   - Right-click project → Import
   - General → File System
   - Browse to source directory
   - Import all Java files maintaining package structure

4. **Run Configuration**
   - Right-click `CCRMApplication.java`
   - Run As → Java Application
   - Set VM arguments: `-ea` (enable assertions)

## Running the Application

### Command Line Execution

1. **Compile the Project**
   ```bash
   # Navigate to project root
   cd ccrm
   
   # Compile all Java files
   javac -d bin -cp src src/edu/ccrm/cli/CCRMApplication.java
   ```

2. **Run the Application**
   ```bash
   # Run with assertions enabled
   java -ea -cp bin edu.ccrm.cli.CCRMApplication
   ```

3. **Alternative: Using package structure**
   ```bash
   # From src directory
   cd src
   javac edu/ccrm/cli/CCRMApplication.java
   java -ea edu.ccrm.cli.CCRMApplication
   ```

### IDE Execution (Eclipse)

1. Navigate to `src/edu/ccrm/cli/CCRMApplication.java`
2. Right-click → Run As → Java Application
3. For assertions: Run → Run Configurations → Arguments → VM arguments: `-ea`

## Project Structure

```
ccrm/
├── src/
│   └── edu/
│       └── ccrm/
│           ├── cli/                    # Command Line Interface
│           │   └── CCRMApplication.java
│           ├── config/                 # Configuration (Singleton)
│           │   └── AppConfig.java
│           ├── domain/                 # Domain Models
│           │   ├── Person.java         # Abstract base class
│           │   ├── Student.java        # Student entity
│           │   ├── Instructor.java     # Instructor entity
│           │   ├── Course.java         # Course entity (Builder pattern)
│           │   ├── Enrollment.java     # Enrollment relationship
│           │   ├── Name.java           # Immutable value object
│           │   ├── Grade.java          # Grade enumeration
│           │   ├── Semester.java       # Semester enumeration
│           │   ├── DuplicateEnrollmentException.java
│           │   └── MaxCreditLimitExceededException.java
│           ├── service/                # Business Logic
│           │   ├── Persistable.java    # Generic persistence interface
│           │   ├── Searchable.java     # Search interface with default methods
│           │   ├── StudentService.java
│           │   ├── CourseService.java
│           │   └── EnrollmentService.java
│           ├── io/                     # I/O Operations (NIO.2)
│           │   ├── ImportExportService.java
│           │   └── BackupService.java
│           └── util/                   # Utilities
│               ├── Comparators.java    # Lambda-based comparators
│               └── Validators.java     # Input validation
├── test-data/                         # Sample CSV files
│   ├── students.csv
│   ├── courses.csv
│   └── instructors.csv
├── data/                              # Application data
├── backup/                            # Backup storage
├── screenshots/                       # Setup screenshots
└── README.md
```

## Technical Implementation

### Technical Requirements Coverage

| Syllabus Topic | Implementation Location | Description |
|----------------|------------------------|-------------|
| **Platform & Setup** |
| Evolution of Java | README.md | Timeline of major Java releases |
| Java ME vs SE vs EE | README.md, CCRMApplication | Comparison table and runtime display |
| JDK/JRE/JVM | README.md | Architecture diagram and explanations |
| Installation Steps | README.md, screenshots/ | Windows setup with screenshots |
| Eclipse Setup | README.md, screenshots/ | Project creation and run configs |
| **Language Fundamentals** |
| Syntax & Structure | CCRMApplication.java | Main class with clear package structure |
| Packages | All source files | edu.ccrm.* package hierarchy |
| Variables & Operators | Multiple classes | Primitive and object variables |
| Decision Structures | CCRMApplication.java | if/else, switch statements |
| Loops | Multiple classes | while, do-while, for, enhanced for |
| Arrays | Validators.java, various | Array processing and utilities |
| Strings | Multiple classes | String manipulation and validation |
| **Object-Oriented Programming** |
| Encapsulation | All domain classes | Private fields with getters/setters |
| Inheritance | Person → Student/Instructor | Abstract base class inheritance |
| Abstraction | Person.java, interfaces | Abstract classes and methods |
| Polymorphism | Service classes | Interface implementation |
| Access Modifiers | All classes | private, protected, default, public |
| Constructor Types | Student.java, Course.java | Default, parameterized, copy constructors |
| Immutability | Name.java | Final fields, defensive copying |
| Nested Classes | Student.StudentStats | Static nested and inner classes |
| **Advanced Concepts** |
| Interfaces | Persistable.java, Searchable.java | Interface design with default methods |
| Diamond Problem | Searchable.java | Default method resolution |
| Functional Interfaces | Comparators.java | Lambda expressions and method references |
| Anonymous Classes | CCRMApplication.java | Anonymous inner class for validation |
| Enums | Grade.java, Semester.java | Enums with constructors and methods |
| Upcasting/Downcasting | Service classes | Type casting with instanceof |
| Method Overriding | All domain classes | toString(), equals(), hashCode() |
| Design Patterns | AppConfig.java, Course.java | Singleton and Builder patterns |
| **Exception Handling** |
| Exception Types | README.md | Errors vs Exceptions explanation |
| Custom Exceptions | DuplicateEnrollmentException.java | Checked and unchecked exceptions |
| Try-Catch-Finally | Multiple classes | Exception handling patterns |
| Multi-catch | Service classes | Multiple exception types |
| Assertions | Validators.java | Assertion usage for invariants |
| **File I/O & NIO.2** |
| Path & Files API | ImportExportService.java | Modern file operations |
| Streams | Multiple I/O classes | Reading/writing with streams |
| DirectoryStream | BackupService.java | Directory traversal |
| Recursive Operations | BackupService.java | Recursive file size calculation |
| **Date/Time API** |
| LocalDate/DateTime | Multiple classes | Modern date/time handling |
| Formatting | AppConfig.java | Date formatters |
| **Functional Programming** |
| Lambdas | Comparators.java | Lambda expressions |
| Method References | Multiple classes | Method reference usage |
| Stream API | Service classes | Data processing pipelines |
| Predicates | Service classes | Filtering operations |

### Design Patterns Implementation

#### 1. Singleton Pattern (`AppConfig.java`)
```java
// Thread-safe singleton using enum helper
private static enum SingletonHelper {
    INSTANCE;
    private final AppConfig appConfig = new AppConfig();
}

public static AppConfig getInstance() {
    return SingletonHelper.INSTANCE.appConfig;
}
```

#### 2. Builder Pattern (`Course.java`)
```java
// Fluent builder for complex object construction
Course course = new Course.Builder("CSE101", "Programming")
    .credits(3)
    .department("Computer Science")
    .semester(Semester.FALL)
    .maxCapacity(50)
    .build();
```

### Exception Handling Strategy

#### Custom Exception Hierarchy
- **Checked Exceptions**: `DuplicateEnrollmentException`
- **Unchecked Exceptions**: `MaxCreditLimitExceededException`
- **Business Rule Validation**: Input validation with detailed error messages

#### Exception Handling Patterns
```java
try {
    enrollmentService.enrollStudent(studentId, courseCode);
} catch (DuplicateEnrollmentException e) {
    // Handle duplicate enrollment
} catch (MaxCreditLimitExceededException e) {
    // Handle credit limit violation
} catch (Exception e) {
    // Handle general exceptions
}
```

### Stream API Usage Examples

#### Student Statistics
```java
// Calculate average GPA using streams
public double getAverageGPA() {
    return students.values().stream()
                  .mapToDouble(Student::calculateGPA)
                  .average()
                  .orElse(0.0);
}
```

#### Course Filtering
```java
// Filter courses by multiple criteria
public List<Course> findAvailableCourses() {
    return courses.values().stream()
                 .filter(Course::isActive)
                 .filter(course -> !course.isFull())
                 .collect(Collectors.toList());
}
```

## Features

### Core Functionality

#### Student Management
- ✅ Add/Update/Deactivate students
- ✅ Registration number validation
- ✅ GPA calculation and tracking
- ✅ Course enrollment management
- ✅ Student profile and transcript display

#### Course Management
- ✅ Course creation with Builder pattern
- ✅ Instructor assignment
- ✅ Capacity management
- ✅ Search by department, instructor, semester
- ✅ Enrollment tracking

#### Enrollment System
- ✅ Business rule validation
- ✅ Credit limit enforcement
- ✅ Duplicate enrollment prevention
- ✅ Grade recording and management

#### File Operations
- ✅ CSV import/export with NIO.2
- ✅ Automatic backup with timestamps
- ✅ Recursive directory operations
- ✅ File size calculation and formatting

#### Reporting System
- ✅ Statistical reports with Stream API
- ✅ GPA distribution analysis
- ✅ Top students ranking
- ✅ Department-wise course distribution

### Advanced Features

#### Data Validation
- ✅ Regex-based input validation
- ✅ Email format verification
- ✅ Date range validation
- ✅ Business rule enforcement

#### Search & Filtering
- ✅ Flexible search with predicates
- ✅ Lambda-based filtering
- ✅ Comparator-based sorting
- ✅ Multi-criteria search

#### System Features
- ✅ Platform information display
- ✅ Memory usage monitoring
- ✅ Backup management
- ✅ Configuration management

## Usage Examples

### Sample Application Flow

1. **Initial Setup**
   ```
   Welcome to Campus Course & Records Manager (CCRM)
   Loading sample data from test-data directory...
   ```

2. **Add a New Student**
   ```
   Enter Student ID: STU006
   Enter Registration Number: 2024CSE004
   Enter First Name: Emily
   Enter Last Name: Davis
   Enter Email: emily.davis@example.com
   Enter Date of Birth (YYYY-MM-DD): 2003-04-15
   Student added successfully!
   ```

3. **Enroll Student in Course**
   ```
   Enter Student ID: STU006
   Enter Course Code: CSE101
   Student enrolled successfully!
   ```

4. **Record Grade**
   ```
   Enter Student ID: STU006
   Enter Course Code: CSE101
   Available Grades: S, A, B, C, D, E, F
   Enter Grade: A
   Grade recorded successfully!
   ```

5. **Generate Transcript**
   ```
   ============================================================
   ACADEMIC TRANSCRIPT
   ============================================================
   Student: Emily Davis
   Registration No: 2024CSE004
   ============================================================
   Course       Title                          Credits Grade
   ────────────────────────────────────────────────────────
   CSE101       Introduction to Programming    3       A
   ────────────────────────────────────────────────────────
   Overall GPA: 9.00
   ```

### Backup Operations
```
Creating backup...
Backup created: backup-2024-01-15_14-30-25
Backup size: 2.4 KB
Files: 15

Backup Sizes (Calculated Recursively):
backup-2024-01-15_14-30-25  2.4 KB
  Contents (max depth 2):
    backup-2024-01-15_14-30-25/
      data/
        students.csv
        courses.csv
        enrollments.csv
```

## Testing

### Enabling Assertions
To enable assertion checking, run the application with the `-ea` flag:

```bash
java -ea edu.ccrm.cli.CCRMApplication
```

### Sample Assertions in Code
```java
// Precondition assertions in validators
assert id != null : "Student ID cannot be null";
assert regNo != null : "Registration number cannot be null";

// Business rule assertions
assert credits > 0 : "Credits must be positive";
assert maxCapacity > 0 : "Max capacity must be positive";
```

### Test Data
The `test-data/` directory contains sample CSV files for testing:
- `students.csv`: 5 sample students
- `courses.csv`: 8 sample courses
- `instructors.csv`: 6 sample instructors

### Manual Testing Scenarios

1. **Input Validation Testing**
   - Try invalid email formats
   - Test registration number patterns
   - Verify date range validation

2. **Business Rule Testing**
   - Attempt duplicate enrollment
   - Exceed credit limits
   - Test course capacity limits

3. **File Operations Testing**
   - Import/export data
   - Create backups
   - Test recursive operations

4. **Exception Handling Testing**
   - Trigger custom exceptions
   - Test error recovery
   - Verify assertion behavior

## System Requirements

### Runtime Requirements
- Java 8 or higher (Java 17+ recommended)
- 512MB RAM minimum (2GB recommended)
- 50MB disk space for application
- Additional space for data and backups

### Development Requirements
- JDK 8 or higher
- IDE: Eclipse, IntelliJ IDEA, or VS Code
- Git for version control

## Troubleshooting

### Common Issues

1. **"Class not found" error**
   - Verify CLASSPATH includes source directory
   - Check package declarations match directory structure

2. **Assertion errors not showing**
   - Run with `-ea` flag to enable assertions
   - Verify assertions are properly written

3. **File I/O issues**
   - Check file permissions
   - Verify directory paths exist
   - Ensure CSV format is correct

4. **Memory issues with large datasets**
   - Increase heap size: `-Xmx2g`
   - Use streaming for large file operations

## Acknowledgments

### Educational References
- Oracle Java Documentation
- Java: The Complete Reference by Herbert Schildt
- Effective Java by Joshua Bloch
- Design Patterns: Elements of Reusable Object-Oriented Software

### Technologies Used
- **Java SE**: Core platform for application development
- **NIO.2**: Modern file I/O operations
- **Stream API**: Functional programming and data processing
- **Lambda Expressions**: Functional interfaces and method references

### Development Tools
- **Eclipse IDE**: Primary development environment
- **Git**: Version control system
- **Markdown**: Documentation format

---

**Note**: This project is developed for educational purposes to demonstrate comprehensive Java SE programming concepts and best practices.

**Author**: Student Name  
**Course**: Programming in Java  
**Date**: 2024  
**Version**: 1.0.0