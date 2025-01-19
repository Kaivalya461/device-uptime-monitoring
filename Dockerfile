FROM amazoncorretto:11-alpine
COPY target/device-uptime-monitoring-*.jar app.jar

ENTRYPOINT ["java","-Dspring.profiles.active=gke,prod","-jar","/app.jar"]