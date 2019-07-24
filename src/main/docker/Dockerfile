FROM openjdk:8-jdk-alpine

ADD target/*.jar /
RUN mv /*.jar /application.jar


ENV JAVA_OPTIONS="${JAVA_OPTIONS} -Djava.security.egd=file:/dev/./urandom -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8765"

ENTRYPOINT exec java ${JAVA_OPTIONS} -jar /application.jar

#VOLUME ["/config"]
EXPOSE 8080 9080
EXPOSE 8088 9088
