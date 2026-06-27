FROM gradle:9.5-jdk17 AS build
WORKDIR /app
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY src ./src
RUN gradle installDist --no-daemon -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/install/app /app
ENTRYPOINT ["/app/bin/app"]
ENTRYPOINT ["/app/bin/app"]