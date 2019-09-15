package io.github.rscai.microservices.product.model;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class ProductVariant {

  @Id
  private String id;
  private String inventoryItemId;
  @DBRef
  private List<ProductImage> images;
  private Date createdAt;
  private Date updatedAt;
}
