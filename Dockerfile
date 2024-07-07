FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=build/libs/faq-*.jar
COPY ${JAR_FILE} app.jar
ENV LOG_FILE "application.log"
ENV LOGBACK_ROLLINGPOLICY_MAX_FILE_SIZE "10MB"
ENV LOGBACK_ROLLINGPOLICY_MAX_HISTORY "30"
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080