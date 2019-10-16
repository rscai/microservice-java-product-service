package io.github.rscai.microservices.catalog.model;

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
public class Product {

  @Id
  private String id;
  private String title;
  private List<String> tags;
  @DBRef
  private List<ProductImage> images;
  private Date createdAt;
  private Date updatedAt;
}
