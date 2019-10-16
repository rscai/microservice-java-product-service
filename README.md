# Product Service

## Metrics

Item|Status
--|------------
CI|[![Build Status](https://travis-ci.org/rscai/microservices-catalog.svg?branch=master)](https://travis-ci.org/rscai/microservices-catalog)
Code Quality|[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=rscai_microservices-catalog&metric=alert_status)](https://sonarcloud.io/dashboard?id=rscai_microservices-catalog)
Coverage|[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=rscai_microservices-catalog&metric=coverage)](https://sonarcloud.io/dashboard?id=rscai_microservices-catalog)
Line of Code|[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=rscai_microservices-catalog&metric=ncloc)](https://sonarcloud.io/dashboard?id=rscai_microservices-catalog)
Technical Debt|[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=rscai_microservices-catalog&metric=sqale_index)](https://sonarcloud.io/dashboard?id=rscai_microservices-catalog)

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