package io.github.rscai.microservices.catalog.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rscai.microservices.catalog.CatalogApplication;
import io.github.rscai.microservices.catalog.RestDocsMockMvcConfiguration;
import io.github.rscai.microservices.catalog.model.ProductImage;
import io.github.rscai.microservices.catalog.repository.ProductImageRepository;
import java.util.Date;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.hypermedia.HypermediaDocumentation;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@Import(RestDocsMockMvcConfiguration.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = CatalogApplication.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ProductTest {

  private static final String ENDPOINT = "/products";

  @Autowired
  private MockMvc mvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private ProductImageRepository imageRepository;

  private String imageAId;
  private String imageBId;
  private String imageCId;

  private String variantAId;
  private String variantBId;
  private String variantCId;

  private static LinksSnippet links(LinkDescriptor... descriptors) {
    return HypermediaDocumentation.links(halLinks(), linkWithRel("self").description("self link"),
        linkWithRel("product").description("self link"),
        linkWithRel("images").description("related images")).and(descriptors);
  }

  private static RequestFieldsSnippet requestFields(FieldDescriptor... descriptors) {
    return PayloadDocumentation.requestFields(
        fieldWithPath("title").type(JsonFieldType.STRING).description("catalog's title"),
        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("tags"),
        fieldWithPath("createdAt").type("Date").description("create timestamp").optional()
            .ignored(),
        fieldWithPath("updatedAt").type("Date").description("last update timestamp").optional()
            .ignored()).and(descriptors);
  }

  private static ResponseFieldsSnippet responseFields(FieldDescriptor... descriptors) {
    return PayloadDocumentation.responseFields(
        fieldWithPath("title").type(JsonFieldType.STRING).description("catalog's title"),
        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("tags"),
        fieldWithPath("createdAt").type("Date").description("create timestamp"),
        fieldWithPath("updatedAt").type("Date").description("last update timestamp"),
        subsectionWithPath("_links").description("links to other resources")).and(descriptors);
  }

  @Before
  public void setUp() {
    ProductImage imageA = new ProductImage();
    imageA.setSrc("https://aaa.bbb.ccc/ddd.png");
    imageA.setCreatedAt(new Date());
    imageA.setUpdatedAt(new Date());

    imageAId = imageRepository.save(imageA).getId();

    ProductImage imageB = new ProductImage();
    imageB.setSrc("https://bbb.ccc.ddd/eee.png");
    imageB.setCreatedAt(new Date());
    imageB.setUpdatedAt(new Date());

    imageBId = imageRepository.save(imageB).getId();

    ProductImage imageC = new ProductImage();
    imageC.setSrc("https://ccc.ddd.eee/fff.png");
    imageC.setCreatedAt(new Date());
    imageC.setUpdatedAt(new Date());

    imageCId = imageRepository.save(imageC).getId();
  }

  @Test
  public void testCreateAndGet() throws Exception {
    final String imageALink = obtainLinkOfImage(imageAId);
    final String imageBLink = obtainLinkOfImage(imageBId);
    
    final String title = "New Product";
    final String ELECTRONICS = "Electronics";
    final String MOBILE = "Mobile";

    String createResponse = mvc.perform(
        post(ENDPOINT).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
            .content(String.format(
                "{\"title\":\"%s\",\"tags\":[\"%s\",\"%s\"],\"images\":[\"%s\",\"%s\"]}",
                title, ELECTRONICS, MOBILE, imageALink, imageBLink)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.title", is(title)))
        .andExpect(jsonPath("$.createdAt", notNullValue()))
        .andExpect(jsonPath("$.updatedAt", notNullValue()))
        .andExpect(jsonPath("$._links.images", notNullValue()))
        .andDo(document("product/create", links(), requestFields(
            fieldWithPath("images").type(JsonFieldType.ARRAY)
                .description("links of referred ProductImage")),responseFields()))
        .andReturn().getResponse().getContentAsString();

    String productId = Stream
        .of(objectMapper.readTree(createResponse).at("/_links/self/href").asText().split("/"))
        .reduce((first, second) -> second).orElse(null);

    mvc.perform(get(ENDPOINT + "/{id}", productId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.title", is(title)))
        .andExpect(jsonPath("$.createdAt", notNullValue()))
        .andExpect(jsonPath("$.updatedAt", notNullValue()))
        .andExpect(jsonPath("$._links.images", notNullValue()))
        .andDo(document("product/getOne", links(),
            pathParameters(parameterWithName("id").description("catalog's id")), responseFields()));

    mvc.perform(get(ENDPOINT + "/{id}/images", productId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$._embedded.productImages", hasSize(2)))
        .andDo(document("product/getImages",
            pathParameters(parameterWithName("id").description("catalog's id"))));
  }

  @Test
  public void testUpdate() throws Exception {
    // preset catalog
    final String imageALink = obtainLinkOfImage(imageAId);
    final String imageBLink = obtainLinkOfImage(imageBId);

    final String title = "New Product";
    final String ELECTRONICS = "Electronics";
    final String MOBILE = "Mobile";

    String createResponse = mvc.perform(
        post(ENDPOINT).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
            .content(String.format(
                "{\"title\":\"%s\",\"tags\":[\"%s\",\"%s\"],\"images\":[\"%s\",\"%s\"]}",
                title, ELECTRONICS, MOBILE, imageALink, imageBLink)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    String productId = Stream
        .of(objectMapper.readTree(createResponse).at("/_links/self/href").asText().split("/"))
        .reduce((first, second) -> second).orElse(null);

    final String imageCLink = obtainLinkOfImage(imageCId);

    final String newTitle = "New Title";

    mvc.perform(put(ENDPOINT + "/{id}", productId).accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON).content(String.format(
            "{\"title\":\"%s\",\"tags\":[\"%s\"]}",
            newTitle, MOBILE)))
        .andExpect(status().isOk()).andDo(document("product/update", links(),
        pathParameters(parameterWithName("id").description("catalog's id")), PayloadDocumentation
            .requestFields(
                fieldWithPath("title").type(JsonFieldType.STRING).description("catalog's title"),
                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("tag list")),
        responseFields()));

    mvc.perform(get(ENDPOINT + "/{id}", productId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.title", is(newTitle)))
        .andExpect(jsonPath("$.tags", hasSize(1))).andExpect(jsonPath("$.tags[0]", is(MOBILE)));

    mvc.perform(put(ENDPOINT + "/{id}/images", productId)
        .contentType(MediaType.parseMediaType("text/uri-list"))
        .content(imageBLink + "\n" + imageCLink)).andExpect(status().isNoContent()).andDo(
        document("product/updateImages",
            pathParameters(parameterWithName("id").description("catalog's id"))));

    mvc.perform(get(ENDPOINT + "/{id}/images", productId).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$._embedded.productImages", hasSize(2)))
        .andExpect(jsonPath("$._embedded.productImages[0]._links.self.href", endsWith(imageBId)))
        .andExpect(jsonPath("$._embedded.productImages[1]._links.self.href", endsWith(imageCId)));
      }

  @Test
  public void testDelete() throws Exception {
    // preset catalog
    final String imageALink = obtainLinkOfImage(imageAId);
    final String imageBLink = obtainLinkOfImage(imageBId);

    final String title = "New Product";
    final String ELECTRONICS = "Electronics";
    final String MOBILE = "Mobile";

    String createResponse = mvc.perform(
        post(ENDPOINT).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
            .content(String.format(
                "{\"title\":\"%s\",\"tags\":[\"%s\",\"%s\"],\"images\":[\"%s\",\"%s\"]}",
                title, ELECTRONICS, MOBILE, imageALink, imageBLink)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    String productId = Stream
        .of(objectMapper.readTree(createResponse).at("/_links/self/href").asText().split("/"))
        .reduce((first, second) -> second).orElse(null);

    mvc.perform(delete(ENDPOINT + "/{id}", productId)).andExpect(status().isNoContent()).andDo(
        document("product/delete",
            pathParameters(parameterWithName("id").description("catalog's id"))));

    mvc.perform(get(ENDPOINT + "/{id}", productId)).andExpect(status().isNotFound());
  }

  private String obtainLinkOfImage(final String imageId) throws Exception {
    String responseContent = mvc
        .perform(get("/productImages/{id}", imageId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    return objectMapper.readTree(responseContent).at("/_links/self/href").asText();
  }
}
