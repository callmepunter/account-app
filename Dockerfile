FROM openjdk:8-jdk-alpine
ADD target/*.jar /application.jar
ENV JAVA_OPTIONS="${JAVA_OPTIONS} -Dspring.profiles.active=dev -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8086 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT exec java ${JAVA_OPTIONS} -jar /application.jar
EXPOSE 8085 8085
EXPOSE 8086 8086
