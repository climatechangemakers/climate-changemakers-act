# App Building phase --------
FROM openjdk:11.0.12 AS build

RUN mkdir /appbuild
COPY . /appbuild

WORKDIR /appbuild

RUN ./gradlew clean build --no-daemon
# End App Building phase --------

# Container setup --------
FROM openjdk:11.0.12-jre

# Creating user
ENV APPLICATION_USER 1033
RUN useradd $APPLICATION_USER

# Giving permissions
RUN mkdir /app
RUN mkdir /app/resources
RUN chown -R $APPLICATION_USER /app
RUN chmod -R 755 /app

# Setting user to use when running the image
USER $APPLICATION_USER

# Copying needed files
COPY --from=build /appbuild/build/libs/climatechangemakers-backend*all.jar /app/climatechangemakers-backend.jar
COPY --from=build /appbuild/build/resources/ /app/resources/
WORKDIR /app

# Entrypoint definition
CMD ["sh", "-c", "java -server -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -XX:InitialRAMFraction=2 -XX:MinRAMFraction=2 -XX:MaxRAMFraction=2 -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication -jar climatechangemakers-backend.jar"]
# End Container setup --------
