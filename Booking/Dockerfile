FROM openjdk:17
WORKDIR /booking-service
COPY . .
RUN microdnf install -y maven
RUN mvn clean install
ENTRYPOINT ["java" ,"-jar","target/booking-service-0.0.1-SNAPSHOT.jar"]