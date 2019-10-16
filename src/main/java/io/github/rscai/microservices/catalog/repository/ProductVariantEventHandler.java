package io.github.rscai.microservices.catalog.repository;

import io.github.rscai.microservices.catalog.model.ProductVariant;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(ProductVariant.class)
public class ProductVariantEventHandler {

  @Autowired
  private ProductVariantRepository entityRepository;

  @HandleBeforeCreate
  public void handleProductVariantCreate(ProductVariant entity) {
    if (entity.getCreatedAt() == null) {
      entity.setCreatedAt(new Date());
    }
    entity.setUpdatedAt(new Date());
  }

  @HandleBeforeSave
  public void handleProductVariantBeforeSave(ProductVariant entity) {
    final Optional<ProductVariant> existedEntity = entityRepository.findById(entity.getId());
    if (existedEntity.isPresent()) {
      entity.setCreatedAt(existedEntity.get().getCreatedAt());
    }
    entity.setUpdatedAt(new Date());
  }
}
