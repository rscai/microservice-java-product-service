package io.github.rscai.microservices.product.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class ProductImage {
  @Id
  private String id;
  private String src;
  private Date createdAt;
  private Date updatedAt;
}
