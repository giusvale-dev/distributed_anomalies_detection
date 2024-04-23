#!/bin/bash

# Variables
PROJECT_NAME="watchdog-1.0-SNAPSHOT.jar"
MAIN_CLASS="it.uniroma1.App"
JAR_FILE="${PROJECT_NAME}"
DEPENDENCIES_DIR="libs" # Directory to copy dependencies

# Path to Maven installation
MAVEN_HOME="/usr/bin/mvn" 

# Clean and build the project using Maven
echo "Building the project..."
$MAVEN_HOME clean package -DskipTests=true

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "Maven build failed. Exiting..."
    exit 1
fi

# Copy dependencies to the specified directory using Maven Dependency Plugin
echo "Copying dependencies to target/${DEPENDENCIES_DIR} directory..."
$MAVEN_HOME dependency:copy-dependencies -DoutputDirectory="target/${DEPENDENCIES_DIR}"

# Check if copying dependencies was successful
if [ $? -ne 0 ]; then
    echo "Copying dependencies failed. Exiting..."
    exit 1
fi

# Run the main class with the classpath set to include the project JAR and dependencies
echo "Running the main class..."
#echo "Command: java -cp \"target/${JAR_FILE}:${DEPENDENCIES_DIR}/*\" \"${MAIN_CLASS}\""
java -cp "target/${JAR_FILE}:target/${DEPENDENCIES_DIR}/*" "${MAIN_CLASS}"

# Print a message when the script finishes
echo "Application has finished running."
