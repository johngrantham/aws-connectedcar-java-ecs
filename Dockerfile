FROM maven:3.8.7-openjdk-18 AS build
ARG TOKEN
RUN mkdir -p /root/.m2 && mkdir /root/.m2/repository
COPY deployment/maven/settings.xml /root/.m2
RUN sed -i "s/TOKEN/${TOKEN}/g" /root/.m2/settings.xml
WORKDIR /src
COPY main /src/main/
RUN mvn -q -f /src/main/pom.xml clean package

FROM openjdk:11
RUN adduser \
  --disabled-password \
  --home /app \
  --gecos '' app \
  && chown -R app /app
USER app
WORKDIR /app
COPY --from=build /src/main/apis/target/apis-APIS-SNAPSHOT.jar /app/apis-APIS-SNAPSHOT.jar 
ENV JAVA_OPTS="$JAVA_OPTS -XX:InitialRAMPercentage=10 -XX:MinRAMPercentage=50 -XX:MaxRAMPercentage=80"
EXPOSE 8080
ENTRYPOINT ["java","-cp","/app/apis-APIS-SNAPSHOT.jar","org.johng.connectedcar.container.apis.GrizzlyLauncher"]