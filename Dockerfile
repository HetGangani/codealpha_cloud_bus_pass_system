FROM eclipse-temmurin:21

WORKDIR /app

COPY . .

RUN chmod +x mnvw
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/buspasssystem-0.0.1-SNAPSHOT.jar"]
