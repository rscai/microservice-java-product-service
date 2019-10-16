package io.github.rscai.microservices.catalog.repository;

import io.github.rscai.microservices.catalog.model.ProductVariant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "productVariants", path = "productVariants")
public interface ProductVariantRepository extends MongoRepository<ProductVariant, String> {

}
