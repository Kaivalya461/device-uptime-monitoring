FROM openjdk:11-jdk-alpine
COPY target/device-uptime-monitoring-1.0-SNAPSHOT.jar ping-monitor-service-1.0.jar

ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/ping-monitor-service-1.0.jar"]