FROM openjdk:17
WORKDIR /wallet-service
COPY . .
RUN microdnf install -y maven
RUN mvn clean install
ENTRYPOINT ["java" ,"-jar","target/wallet-service-0.0.1-SNAPSHOT.jar"]