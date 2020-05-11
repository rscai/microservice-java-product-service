package io.github.rscai.microservices.catalog.repository;

import io.github.rscai.microservices.catalog.model.Product;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('SCOPE_catalog.read')")
@RepositoryRestResource(collectionResourceRel = "products", path = "products")
public interface ProductRepository extends MongoRepository<Product, String> {

  String AUTHORITY_CATALOG_WRITE = "hasAuthority('SCOPE_catalog.write')";

  @PreAuthorize(AUTHORITY_CATALOG_WRITE)
  @Override
  <S extends Product> S save(S entity);

  @PreAuthorize(AUTHORITY_CATALOG_WRITE)
  @Override
  <S extends Product> List<S> saveAll(Iterable<S> entities);

  @PreAuthorize(AUTHORITY_CATALOG_WRITE)
  @Override
  void delete(Product entity);

  @PreAuthorize(AUTHORITY_CATALOG_WRITE)
  @Override
  void deleteAll();

  @PreAuthorize(AUTHORITY_CATALOG_WRITE)
  @Override
  void deleteAll(Iterable<? extends Product> entities);

  @PreAuthorize(AUTHORITY_CATALOG_WRITE)
  @Override
  void deleteById(String s);
}
