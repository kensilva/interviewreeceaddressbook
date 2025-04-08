FROM amazoncorretto:21
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENV JVM_OPS=""
CMD java $JVM_OPS -jar /app.jar

