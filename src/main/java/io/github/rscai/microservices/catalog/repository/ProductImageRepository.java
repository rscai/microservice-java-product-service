package io.github.rscai.microservices.catalog.repository;


import io.github.rscai.microservices.catalog.model.ProductImage;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('SCOPE_catalog.read')")
@RepositoryRestResource(collectionResourceRel = "productImages", path = "productImages")
public interface ProductImageRepository extends MongoRepository<ProductImage, String> {
  String AUTHORITY_CATALOG_WRITE = "hasAuthority('SCOPE_catalog.write')";

  @PreAuthorize(AUTHORITY_CATALOG_WRITE)
  @Override
  <S extends ProductImage> S save(S entity);

  @PreAuthorize(AUTHORITY_CATALOG_WRITE)
  @Override
  <S extends ProductImage> List<S> saveAll(Iterable<S> entities);

  @PreAuthorize(AUTHORITY_CATALOG_WRITE)
  @Override
  void delete(ProductImage entity);

  @PreAuthorize(AUTHORITY_CATALOG_WRITE)
  @Override
  void deleteAll();

  @PreAuthorize(AUTHORITY_CATALOG_WRITE)
  @Override
  void deleteAll(Iterable<? extends ProductImage> entities);

  @PreAuthorize(AUTHORITY_CATALOG_WRITE)
  @Override
  void deleteById(String s);
}
