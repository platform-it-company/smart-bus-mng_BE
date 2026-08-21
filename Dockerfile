FROM eclipse-temurin:17-jdk AS build

WORKDIR /workspace
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src
RUN sed -i 's/\r$//' gradlew \
    && chmod +x gradlew \
    && ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:17-jre

ENV TZ=Asia/Seoul

WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app/app.jar"]
