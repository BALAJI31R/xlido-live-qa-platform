FROM gradle:7.4.2-jdk17 AS build

WORKDIR /app
COPY . .
RUN ./gradlew clean installDist -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /app/app/build/install/app ./
COPY sample_input ./sample_input

ENTRYPOINT ["./bin/app"]
CMD ["INPUT_FILE=sample_input/sample_input_one.txt"]
