FROM openjdk:8-jdk-alpine

WORKDIR /app

COPY .mvn/ .mvn

COPY mvnw pom.xml ./

RUN chmod +x ./mvnw && ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw","spring-boot:run"]