# build the client
FROM node:lts-alpine3.14 AS build_client

RUN mkdir -p /app
COPY frontend/package*.json ./

WORKDIR /app

RUN npm install
COPY frontend/ .

RUN npm run build

# build the backend

FROM openjdk:11.0.12 AS build_backend

RUN mkdir /appbuild
COPY backend/ /appbuild

RUN rm -rf /appbuild/src/main/resources/client
RUN mkdir -p /appbuild/src/main/resources/client

COPY --from=build_client /app/build/ /appbuild/src/main/resources/client

WORKDIR /appbuild

# TODO(kcianfarini) this is a bug in SQLDelight, the verifyMainDatabaseMigration task should not run.
# sqldelight github issue 2654
RUN ./gradlew clean build -x test -x verifyMainDatabaseMigration --no-daemon

# Run the server

FROM openjdk:11.0.12-jre

ENV APPLICATION_USER 1033
RUN useradd $APPLICATION_USER

RUN mkdir -p /app/resources
RUN mkdir -p /app/log
RUN chown -R $APPLICATION_USER /app
RUN chmod -R 777 /app

USER $APPLICATION_USER

COPY --from=build_backend /appbuild/build/libs/climatechangemakers-backend*all.jar /app/climatechangemakers-backend.jar
COPY --from=build_backend /appbuild/build/resources/ /app/resources/
WORKDIR /app

ARG cwc_proxy_host

RUN echo "echo ${cwc_proxy_host} cwc.house.gov >> /etc/hosts" >> /tmp/update_hosts.sh
RUN echo "echo ${cwc_proxy_host} soapbox.senate.gov >> /etc/hosts" >> /tmp/update_hosts.sh
RUN chmod 777 /tmp/update_hosts.sh

USER root
ENTRYPOINT ["/bin/sh", "-c", "cat /tmp/update_hosts.sh && /tmp/update_hosts.sh && exec java -server -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -XX:InitialRAMFraction=2 -XX:MinRAMFraction=2 -XX:MaxRAMFraction=2 -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication -jar climatechangemakers-backend.jar"]
