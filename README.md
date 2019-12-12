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
docker run -d --name catalog-mongo -p 27018:27017 -v ~/workspace/microservices-java/catalog-mongo-data:/data/db -e MONGO_INITDB_ROOT_USERNAME=mongoadmin -e MONGO_INITDB_ROOT_PASSWORD=secret mongo:3.4
```

```bash
docker exec -it catalog-mongo mongo -u mongoadmin -p secret --authenticationDatabase admin
```

```bash
MongoDB shell version v3.4.23
connecting to: mongodb://127.0.0.1:27017
MongoDB server version: 3.4.23
> show dbs
admin  0.000GB
local  0.000GB
test   0.000GB
> show collections;
product
productImage
> db.product.find().limit(1);
{ "_id" : ObjectId("5db3a5385cb95ce6e56a9248"), "title" : "iMac", "tags" : [ "mac", " apple" ], "images" : [ DBRef("productImage", ObjectId("5dabf9ab5cb95c4dad891a95")), DBRef("productImage", ObjectId("5dabf9a05cb95c4dad891a94")), DBRef("productImage", ObjectId("5dabf9845cb95c4dad891a92")), DBRef("productImage", ObjectId("5dabf9765cb95c4dad891a91")) ], "createdAt" : ISODate("2019-10-26T01:45:28.703Z"), "updatedAt" : ISODate("2019-11-03T09:31:00.103Z"), "_class" : "io.github.rscai.microservices.catalog.model.Product" }
> 
```

## Run Application

```bash
java -Dspring.profiles.active=dev -jar app.jar
```