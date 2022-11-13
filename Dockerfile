FROM openjdk:8-jdk-alpine
COPY target/device-uptime-monitoring-1.0-SNAPSHOT.jar ping-monitor-service-1.0.jar

ENTRYPOINT ["java","-jar","/ping-monitor-service-1.0.jar"]