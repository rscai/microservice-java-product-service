package io.github.rscai.microservices.catalog.repository;


import io.github.rscai.microservices.catalog.model.ProductImage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "productImages", path = "productImages")
public interface ProductImageRepository extends MongoRepository<ProductImage, String> {

}
