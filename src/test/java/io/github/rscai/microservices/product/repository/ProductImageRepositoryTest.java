package io.github.rscai.microservices.product.repository;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import io.github.rscai.microservices.product.model.ProductImage;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@DataMongoTest
public class ProductImageRepositoryTest {

  @Autowired
  private ProductImageRepository testObject;

  @Test
  public void testSaveAndFindOne() {
    ProductImage newImage = new ProductImage();
    newImage.setId("IMG123");
    newImage.setSrc("https://aaa.bbb.ccc/image.png");
    final Date createdAt = new Date();
    final Date updatedAt = new Date();
    newImage.setCreatedAt(createdAt);
    newImage.setUpdatedAt(updatedAt);
    testObject.save(newImage);

    ProductImage foundOne = testObject.findById("IMG123").get();
    assertThat(foundOne, notNullValue());
    assertThat(foundOne.getSrc(), is("https://aaa.bbb.ccc/image.png"));
    assertThat(foundOne.getCreatedAt(), is(createdAt));
    assertThat(foundOne.getUpdatedAt(), is(updatedAt));
  }
}
