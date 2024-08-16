FROM openjdk:21 AS base

ENV LANGUAGE='en_US:en'

RUN mkdir -p /code

# Dev
FROM base AS dev

ARG JENKINS_GROUP_ID=1000
ARG JENKINS_USER_ID=1000

RUN groupadd --gid "$JENKINS_GROUP_ID" jenkins && \
   useradd --uid "$JENKINS_USER_ID" --gid "$JENKINS_GROUP_ID" --create-home jenkins

COPY infrastructure/docker/entrypoint.sh /entrypoint.sh
ENTRYPOINT [ "/entrypoint.sh" ]

# Build
FROM base AS build

WORKDIR /code

COPY ./ /code
RUN ./mvnw -B -DskipTests install -Dquarkus.package.type=uber-jar -D quarkus.package.output-name=collector

CMD [ "java", "-jar", "target/collector-runner.jar" ]
