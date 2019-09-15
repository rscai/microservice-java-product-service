package io.github.rscai.microservices.product.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rscai.microservices.product.ProductServiceApplication;
import io.github.rscai.microservices.product.model.ProductImage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = ProductServiceApplication.class)
@AutoConfigureMockMvc
public class ProductImageTest {

  private static final String ENDPOINT = "/productImages";
  @Autowired
  private MockMvc mvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testSaveAndGetOne() throws Exception {
    final String src = "https://aaa.bbb.ccc/ddd.png";
    ProductImage newImage = new ProductImage();
    newImage.setSrc(src);

    ArgumentCaptor<String> linkMatcher = ArgumentCaptor.forClass(String.class);
    String responseContent = mvc.perform(
        post(ENDPOINT).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newImage))).andDo(print())
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.src", is(src)))
        .andExpect(jsonPath("$.createdAt", notNullValue()))
        .andExpect(jsonPath("$.updatedAt", notNullValue()))
        .andReturn().getResponse().getContentAsString();
    String newImageLink = objectMapper.readTree(responseContent).at("/_links/self/href").asText();

    mvc.perform(get(newImageLink).accept(MediaType.APPLICATION_JSON)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.src", is(src)));
  }

  @Test
  public void testUpdate() throws Exception {
    final String newSrc = "https://dd.ee.ff/gg.png";
    ProductImage newImage = new ProductImage();
    newImage.setSrc("https://aaa.bbb.ccc/ddd.png");

    String responseContent = mvc.perform(
        post(ENDPOINT).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newImage))).andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    String newImageLink = objectMapper.readTree(responseContent).at("/_links/self/href").asText();

    newImage.setSrc(newSrc);

    mvc.perform(
        put(newImageLink).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newImage))).andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    mvc.perform(get(newImageLink).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(jsonPath("$.src", is(newSrc)))
        .andExpect(jsonPath("$.updatedAt", notNullValue()));
  }
}
