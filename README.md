# Product Service

## Status

CI|Code Quality
--|------------
[![Build Status](https://travis-ci.org/rscai/microservices-catalog.svg?branch=master)](https://travis-ci.org/rscai/microservices-catalog)|

## Mongo DB

```bash
docker run -d --name product-mongo -p 27017:27017 -v ~/workspace/microservices-java/product-mongo-data:/data/db -e MONGO_INITDB_ROOT_USERNAME=mongoadmin -e MONGO_INITDB_ROOT_PASSWORD=secret mongo:3.4
```

```bash
docker exec -it product-mongo mongo -u mongoadmin -p secret --authenticationDatabase admin
```

## Run Application

```bash
java -Dspring.profiles.active=dev -jar app.jar
```