package io.github.rscai.microservices.catalog.repository;

import io.github.rscai.microservices.catalog.model.Product;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Product.class)
public class ProductEventHandler {

  @Autowired
  private ProductRepository entityRepository;

  @HandleBeforeCreate
  public void handleProductCreate(Product entity) {
    if (entity.getCreatedAt() == null) {
      entity.setCreatedAt(new Date());
    }
    entity.setUpdatedAt(new Date());
  }

  @HandleBeforeSave
  public void handleProductBeforeSave(Product entity) {
    final Optional<Product> existedEntity = entityRepository.findById(entity.getId());
    if (existedEntity.isPresent()) {
      entity.setCreatedAt(existedEntity.get().getCreatedAt());
    }
    entity.setUpdatedAt(new Date());
  }
}
