FROM openjdk:17
WORKDIR /user-service
COPY . .
RUN microdnf install -y maven
RUN mvn clean install
ENTRYPOINT ["java" ,"-jar","target/user-service-0.0.1-SNAPSHOT.jar"]