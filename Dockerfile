FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace/app

RUN apt-get update -qq && \
    apt-get install -y --no-install-recommends \
        maven build-essential bash ca-certificates curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ARG GIT_USERNAME
ARG GIT_TOKEN

RUN mkdir -p ~/.m2 && \
    echo '<settings> <servers> <server> <id>github</id> <username>'$GIT_USERNAME'</username> <password>'$GIT_TOKEN'</password> </server> </servers> </settings>' > ~/.m2/settings.xml

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre

WORKDIR /app
VOLUME /tmp

COPY --from=build /workspace/app/target/vetcare-0.0.1.jar app.jar
COPY src/main/resources/application.yaml application.yaml

ENTRYPOINT ["java", \
  "-XX:MinRAMPercentage=60", \
  "-XX:MaxRAMPercentage=85", \
  "-XX:+OptimizeStringConcat", \
  "-jar", "app.jar"]