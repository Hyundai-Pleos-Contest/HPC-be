FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace
COPY gradlew gradlew.bat build.gradle settings.gradle gradle.properties gradle.lockfile ./
COPY gradle ./gradle
COPY src ./src
RUN sh ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN groupadd --system hpc && useradd --system --gid hpc hpc
COPY --from=build --chown=hpc:hpc /workspace/build/libs/hpc-be.jar /app/hpc-be.jar
USER hpc
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/hpc-be.jar"]
