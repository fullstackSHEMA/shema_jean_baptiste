# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy all project files
COPY . /app

# Compile all Java files
RUN javac *.java

# Run InternshipManagementSystem by default
CMD ["java", "InternshipManagementSystem"]