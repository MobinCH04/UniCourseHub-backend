FROM openjdk:26-ea-21-jdk-oraclelinux8

WORKDIR /

ENV TZ=Asia/Tehran

COPY target/uniCourseHub-0.0.1-SNAPSHOT.jar /app/uniCourseHub.jar

EXPOSE 8081

CMD ["java","-Duser.timezone=Asia/Tehran", "-jar", "/app/uniCourseHub.jar"]