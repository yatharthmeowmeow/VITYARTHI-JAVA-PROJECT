#!/bin/bash

# CCRM Application Launcher Script
# This script helps run the CCRM application interactively

echo "=== CCRM Application Launcher ==="
echo "Preparing to launch Campus Course & Records Manager..."
echo ""

# Navigate to the source directory
cd "$(dirname "$0")/src"

# Clean up old class files
echo "🧹 Cleaning up old class files..."
find . -name "*.class" -delete

# Check if compiled classes exist
if [ ! -f "edu/ccrm/cli/CCRMApplication.class" ]; then
    echo "Compiling Java sources..."
    javac edu/ccrm/cli/CCRMApplication.java
    
    if [ $? -ne 0 ]; then
        echo "❌ Compilation failed!"
        exit 1
    fi
    echo "✅ Compilation successful!"
fi

echo "🚀 Starting CCRM Application..."
echo "   Use Ctrl+C to exit at any time"
echo "   The application will show a menu - enter your choice and press Enter"
echo ""

# Run the application with assertions enabled
exec java -ea edu.ccrm.cli.CCRMApplication