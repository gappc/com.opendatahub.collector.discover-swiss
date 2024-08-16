# Open Data Hub collector for discover.swiss

This project implements a proof-of concept implementation of an [Open Data Hub](https://opendatahub.com/) collector to import data from [discover.swiss](https://discover.swiss/).

> **Important**: being a POC, this project supports only imports for collection data from the [discoverswiss-prod-v2-infocenter-api](discoverswiss-prod-v2-infocenter-api) API (e.g. [https://developer.discover.swiss/api-details#api=discoverswiss-prod-v2-infocenter-api&operation=List-Events](https://developer.discover.swiss/api-details#api=discoverswiss-prod-v2-infocenter-api&operation=List-Events)).

The collector is implemented as a REST service that can be used to import data from the discover.swiss API into a MongoDB database.

[Quarkus](https://quarkus.io) is used as the underlying framework to implement the collector.

## Table of Contents

- [Quick start](#quick-start)
- [Development](#development)
- [Import data from discover.swiss](#import-data-from-discoverswiss)
- [Build for production](#build-for-production)

## Quick start

Following the instructions below will start the collector, MongoDB and MongoDB Express as Docker containers using Docker Compose. It is the quickest and easiest way to get the everything up and running.

> Note, that this setup is **NOT RECOMMENDED FOR PRODUCTION**, as it:
>
> - opens the ports to the collector, MongoDB and MongoDB Express to the world. In production, you should use a reverse proxy to expose the collector to the world and keep the MongoDB and MongoDB Express ports closed.
> - starts the collector in development mode, which is not suitable for production. For production, you should build the collector as an uber JAR or a native executable (see [Build for production](#build-for-production)).

Prerequisites:

- A valid API key for the [discover.swiss API](https://developer.discover.swiss/)
- [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/)

Run the following commands:

```bash
# Get a copy of the repository, e.g. by cloning it from the following location
git clone https://github.com/gappc/com.opendatahub.collector.discover-swiss

# Change into the project directory
cd com.opendatahub.collector.discover-swiss

# Copy the file .env.example to .env and adjust the configuration parameters, e.g. the API key for discover.swiss
cp .env.example .env

# Copy compose.yml.example to compose.yml to open the ports
# to the collector, MongoDB and MongoDB Express
#
# DON'T DO THIS IN PRODUCTION, AS IT OPENS THE PORTS TO THE WORLD!
#
cp compose.override.yml.example compose.override.yml

# Start the collector, MongoDB and MongoDB Express as Docker containers using Docker Compose
# The first start may take a while, as the collector will download all dependencies
# TODO: fix this
docker compose up
```

You should now be able to [import data from discover.swiss](#import-data-from-discoverswiss).

## Development

Following the instructions below will prepare a local development environment.

Prerequisites:

- A valid API key for the [discover.swiss API](https://developer.discover.swiss/)
- [Java 21 JDK](https://docs.oracle.com/en/java/javase/21/)
- [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/)

Run the following commands:

```bash
# Get a copy of the repository, e.g. by cloning it from the following location
git clone https://github.com/gappc/com.opendatahub.collector.discover-swiss

# Change into the project directory
cd com.opendatahub.collector.discover-swiss

# Copy the file .env.example to .env and adjust the configuration parameters, e.g. the API key for discover.swiss
cp .env.example .env

# Copy compose.yml.example to compose.yml to open the ports
# to the collector, MongoDB and MongoDB Express
#
# DON'T DO THIS IN PRODUCTION, AS IT OPENS THE PORTS TO THE WORLD!
#
cp compose.override.yml.example compose.override.yml

# Start MongoDB and MongoDB Express as Docker containers using Docker Compose
docker compose up mongo mongo-express

# Start the development server for the collector
# Note that you need to set the environment variable MONGO_DB_HOST to "localhost" (or the hostname of your MongoDB instance). By default, it
# is set to "mongo" in the .env file, which is the hostname of the MongoDB instance started by Docker Compose.
MONGO_DB_HOST=localhost ./mvnw quarkus:dev
```

You should now be able to [import data from discover.swiss](#import-data-from-discoverswiss).

## Import data from discover.swiss

After completing the steps from the previous chapters, the following services should run on your maching:

- the collector at [http://localhost:8080](http://localhost:8080)
- a MongoDB Express instance at [http://localhost:8081](http://localhost:8081)
- a MongoDB instance at [mongodb:localhost:27017](mongodb:localhost:27017).

You can now go on and import data from `discover.swiss` by using e.g. the endpoint `http://localhost:8080/api/__DISCOVER_SWISS_COLLECTION__/import`, where `__DISCOVER_SWISS_COLLECTION__` is the name of the collection you want to import.

The following example imports all data from the discover.swiss [events](https://developer.discover.swiss/api-details#api=discoverswiss-prod-v2-infocenter-api&operation=List-Events) collection:

```bash
# Import all events
curl -X POST http://localhost:8080/api/events/import
```

Open up [http://localhost:8081/db/discover_swiss/events](http://localhost:8081/db/discover_swiss/events) to see the imported data in your local MongoDB instance.

## Build for production

### Uber JAR

Run the following command to build an uber JAR for the collector. Note that you need to have a Java 21 JDK installed on your machine.

```bash
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

### Native executable

You can run the following command to build a native executable for the collector. Note that you need to have Docker installed on your machine.

```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

See the [Quarkus documentation](https://quarkus.io/guides/building-native-image) for more information on building native executables.
