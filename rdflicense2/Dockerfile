# syntax=docker/dockerfile:1

# Base image
FROM openjdk:8u111-jdk-alpine

# Set working dir
WORKDIR license2

COPY target/rdflicense2-0.0.1-SNAPSHOT.jar .

# Start
ENTRYPOINT ["java", "-jar", "rdflicense2-0.0.1-SNAPSHOT.jar"]
