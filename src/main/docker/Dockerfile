FROM openjdk:17-oracle
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} application.jar
COPY cacerts usr/java/openjdk-17/lib/security/cacerts
COPY cacerts usr/java/default/lib/security/cacerts
ENTRYPOINT ["java","-XX:MaxRAM=128m", "-Xmx64m", "-Xms32m", "-jar", "application.jar"]