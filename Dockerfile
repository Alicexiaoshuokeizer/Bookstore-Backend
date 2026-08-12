#====================================
#STAGE 1: Build the application
#====================================
#base image
#Use an Ubuntu 24.04-based Eclipse Temurin JDK 25 image as the build environment
#and call this stage build
FROM eclipse-temurin:25-jdk-noble AS build
WORKDIR /app
COPY . .
# use maven wrapper (mvnw) in docker since the project contains mvnw
#so that the project controls the maven version
RUN chmod +x mvnw
# compile and produce .jar
RUN ./mvnw clean package -DskipTests -B

#====================================
#STAGE 2: Run the application
#====================================
FROM eclipse-temurin:25-jre-noble
WORKDIR /app
#copy BookStore-0.0.1-SNAPSHOT.jar from build into the current stage and name it as app.jar
COPY --from=build /app/target/BookStoreBackend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]



