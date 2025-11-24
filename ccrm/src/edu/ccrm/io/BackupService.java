package edu.ccrm.io;

import edu.ccrm.config.AppConfig;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Service for backup operations using NIO.2 and recursive file operations.
 * Demonstrates Path, Files, DirectoryStream, and recursive utilities.
 */
public class BackupService {
    private final AppConfig config;
    
    public BackupService() {
        this.config = AppConfig.getInstance();
    }
    
    // Create backup with timestamped folder
    public Path createBackup() throws IOException {
        String timestamp = LocalDateTime.now().format(config.getTimestampFormatter());
        Path backupRoot = config.getBackupPath();
        Path timestampedBackup = backupRoot.resolve("backup-" + timestamp);
        
        // Create backup directory
        Files.createDirectories(timestampedBackup);
        
        // Copy all data files to backup
        Path dataPath = config.getDataPath();
        if (Files.exists(dataPath)) {
            copyDirectoryRecursively(dataPath, timestampedBackup.resolve("data"));
        }
        
        return timestampedBackup;
    }
    
    // Recursive directory copy using Files.walkFileTree
    private void copyDirectoryRecursively(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) 
                    throws IOException {
                Path targetDir = target.resolve(source.relativize(dir));
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) 
                    throws IOException {
                Path targetFile = target.resolve(source.relativize(file));
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    // Recursive method to calculate total size of backup directory
    public long calculateBackupSize(Path backupPath) throws IOException {
        if (!Files.exists(backupPath)) {
            return 0L;
        }
        
        return calculateDirectorySizeRecursive(backupPath);
    }
    
    // Recursive helper method for calculating directory size
    private long calculateDirectorySizeRecursive(Path directory) throws IOException {
        if (!Files.isDirectory(directory)) {
            return Files.size(directory);
        }
        
        long totalSize = 0L;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    totalSize += calculateDirectorySizeRecursive(entry);
                } else {
                    totalSize += Files.size(entry);
                }
            }
        }
        return totalSize;
    }
    
    // Alternative recursive size calculation using Files.walk
    public long calculateBackupSizeUsingWalk(Path backupPath) throws IOException {
        if (!Files.exists(backupPath)) {
            return 0L;
        }
        
        try (Stream<Path> files = Files.walk(backupPath)) {
            return files.filter(Files::isRegularFile)
                       .mapToLong(path -> {
                           try {
                               return Files.size(path);
                           } catch (IOException e) {
                               return 0L;
                           }
                       })
                       .sum();
        }
    }
    
    // Recursive method to list files by depth
    public void printFilesByDepth(Path directory, int maxDepth) throws IOException {
        printFilesByDepthRecursive(directory, 0, maxDepth);
    }
    
    // Recursive helper for printing files by depth
    private void printFilesByDepthRecursive(Path directory, int currentDepth, int maxDepth) 
            throws IOException {
        if (currentDepth > maxDepth || !Files.exists(directory)) {
            return;
        }
        
        String indent = "  ".repeat(currentDepth);
        System.out.println(indent + directory.getFileName() + 
                          (Files.isDirectory(directory) ? "/" : ""));
        
        if (Files.isDirectory(directory) && currentDepth < maxDepth) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                for (Path entry : stream) {
                    printFilesByDepthRecursive(entry, currentDepth + 1, maxDepth);
                }
            }
        }
    }
    
    // List all backup folders
    public List<Path> listBackupFolders() throws IOException {
        Path backupRoot = config.getBackupPath();
        if (!Files.exists(backupRoot)) {
            return new ArrayList<>();
        }
        
        try (Stream<Path> files = Files.list(backupRoot)) {
            return files.filter(Files::isDirectory)
                       .filter(path -> path.getFileName().toString().startsWith("backup-"))
                       .sorted()
                       .toList();
        }
    }
    
    // Get backup information
    public BackupInfo getBackupInfo(Path backupPath) throws IOException {
        if (!Files.exists(backupPath)) {
            return null;
        }
        
        long size = calculateBackupSize(backupPath);
        LocalDateTime created = LocalDateTime.ofInstant(
            Files.getLastModifiedTime(backupPath).toInstant(),
            java.time.ZoneId.systemDefault()
        );
        
        int fileCount = countFilesRecursive(backupPath);
        
        return new BackupInfo(backupPath, size, created, fileCount);
    }
    
    // Recursive file counting
    private int countFilesRecursive(Path directory) throws IOException {
        if (!Files.isDirectory(directory)) {
            return 1;
        }
        
        int count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                count += countFilesRecursive(entry);
            }
        }
        return count;
    }
    
    // Clean old backups (keep only specified number)
    public int cleanOldBackups(int keepCount) throws IOException {
        List<Path> backups = listBackupFolders();
        if (backups.size() <= keepCount) {
            return 0;
        }
        
        // Remove oldest backups
        int removed = 0;
        for (int i = 0; i < backups.size() - keepCount; i++) {
            deleteDirectoryRecursively(backups.get(i));
            removed++;
        }
        
        return removed;
    }
    
    // Recursive directory deletion
    private void deleteDirectoryRecursively(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) 
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) 
                    throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    // Format file size for display
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    // Inner class for backup information
    public static class BackupInfo {
        private final Path path;
        private final long sizeBytes;
        private final LocalDateTime created;
        private final int fileCount;
        
        public BackupInfo(Path path, long sizeBytes, LocalDateTime created, int fileCount) {
            this.path = path;
            this.sizeBytes = sizeBytes;
            this.created = created;
            this.fileCount = fileCount;
        }
        
        public Path getPath() { return path; }
        public long getSizeBytes() { return sizeBytes; }
        public LocalDateTime getCreated() { return created; }
        public int getFileCount() { return fileCount; }
        public String getFormattedSize() { return formatFileSize(sizeBytes); }
        
        @Override
        public String toString() {
            return String.format("Backup{path='%s', size=%s, files=%d, created=%s}", 
                               path.getFileName(), getFormattedSize(), fileCount, 
                               created.format(AppConfig.getInstance().getTimestampFormatter()));
        }
    }
}