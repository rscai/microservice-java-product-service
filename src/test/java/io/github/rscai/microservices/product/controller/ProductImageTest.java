package io.github.rscai.microservices.product.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rscai.microservices.product.ProductServiceApplication;
import io.github.rscai.microservices.product.model.ProductImage;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.restdocs.hypermedia.HypermediaDocumentation;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = ProductServiceApplication.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ProductImageTest {

  private static final String ENDPOINT = "/productImages";
  @Autowired
  private MockMvc mvc;
  @Autowired
  private ObjectMapper objectMapper;

  private static LinksSnippet links(LinkDescriptor... descriptors) {
    return HypermediaDocumentation.links(halLinks(), linkWithRel("self").description("self link"),
        linkWithRel("productImage").description("self link")).and(descriptors);
  }

  private static RequestFieldsSnippet requestFields(FieldDescriptor... descriptors) {
    return PayloadDocumentation.requestFields(fieldWithPath("id").description("id").ignored(),
        fieldWithPath("src").type(
            JsonFieldType.STRING).description("URL of image"),
        fieldWithPath("createdAt").description("create timestamp").ignored(),
        fieldWithPath("updatedAt").description("update timestamp").ignored()).and(descriptors);
  }

  private static ResponseFieldsSnippet responseFields(FieldDescriptor... descriptors) {
    return PayloadDocumentation
        .responseFields(fieldWithPath("src").type(JsonFieldType.STRING).description("URL of image"),
            fieldWithPath("createdAt").type("Date")
                .description("timestamp of resource created"),
            fieldWithPath("updatedAt").type("Date")
                .description("timestamp of resource updated")).and(descriptors);
  }

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
        .andDo(document("productImage/create",
            links(),
            requestFields(),
            responseFields(
                subsectionWithPath("_links").description("links to other resources"))))
        .andReturn().getResponse().getContentAsString();
    String newImageLink = objectMapper.readTree(responseContent).at("/_links/self/href").asText();
    String newImageId = Stream.of(newImageLink.split("/")).reduce((first, second) -> second)
        .orElse(null);

    mvc.perform(get(ENDPOINT + "/{id}", newImageId).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.src", is(src))).andDo(
        document("productImage/getOne", links(),
            pathParameters(parameterWithName("id").description("image's ID")),
            responseFields(subsectionWithPath("_links").description("links to other resources"))));
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
    String newImageId = Stream.of(newImageLink.split("/")).reduce((first, second) -> second)
        .orElse(null);

    newImage.setSrc(newSrc);

    mvc.perform(
        put(ENDPOINT + "/{id}", newImageId).accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newImage))).andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.src", is(newSrc))).andDo(
        document("productImage/update", links(), requestFields(),
            responseFields(subsectionWithPath("_links").description("links to other resources"))));

    mvc.perform(get(newImageLink).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(jsonPath("$.src", is(newSrc)))
        .andExpect(jsonPath("$.updatedAt", notNullValue()));
  }

  @Test
  public void testDelete() throws Exception {
    ProductImage newImage = new ProductImage();
    newImage.setSrc("https://aaa.bbb.ccc/ddd.png");

    String responseContent = mvc.perform(
        post(ENDPOINT).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newImage))).andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    String newImageLink = objectMapper.readTree(responseContent).at("/_links/self/href").asText();
    String newImageId = Stream.of(newImageLink.split("/")).reduce((first, second) -> second)
        .orElse(null);

    mvc.perform(delete(ENDPOINT + "/{id}", newImageId)).andExpect(status().isNoContent())
        .andDo(document("productImage/delete"));
  }
}
