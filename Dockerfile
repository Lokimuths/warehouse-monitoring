FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY settings.gradle.kts build.gradle.kts gradle.properties ./

RUN chmod +x gradlew && ./gradlew --version --no-daemon

COPY src ./src
RUN ./gradlew installDist --no-daemon -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/install/app /app
ENTRYPOINT ["/app/bin/app"]