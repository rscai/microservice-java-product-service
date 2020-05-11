package io.github.rscai.microservices.catalog.controller;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rscai.microservices.catalog.CatalogApplication;
import io.github.rscai.microservices.catalog.model.ProductImage;
import io.github.rscai.microservices.catalog.repository.ProductImageRepository;
import java.util.Date;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.hypermedia.HypermediaDocumentation;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CatalogApplication.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ProductVariantTest {

  private static final String ENDPOINT = "/productVariants";
  private static final String SCOPE_CATALOG_READ = "SCOPE_catalog.read";
  private static final String SCOPE_CATALOG_WRITE = "SCOPE_catalog.write";

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ProductImageRepository imageRepository;

  private String imageAId;
  private String imageBId;
  private String imageCId;

  private static LinksSnippet links(LinkDescriptor... descriptors) {
    return HypermediaDocumentation.links(halLinks(), linkWithRel("self").description("self link"),
        linkWithRel("productVariant").description("self link"),
        linkWithRel("images").description("links to image")).and(descriptors);
  }

  private static RequestFieldsSnippet requestFields(FieldDescriptor... descriptors) {
    return PayloadDocumentation
        .requestFields(fieldWithPath("id").description("id").optional().ignored(),
            fieldWithPath("inventoryItemId").type(
                JsonFieldType.STRING).description("Item id in inventory service"),
            fieldWithPath("images").description("links to productImage"),
            fieldWithPath("createdAt").description("create timestamp").optional().ignored(),
            fieldWithPath("updatedAt").description("update timestamp").optional().ignored())
        .and(descriptors);
  }

  private static ResponseFieldsSnippet responseFields(FieldDescriptor... descriptors) {
    return PayloadDocumentation
        .responseFields(fieldWithPath("inventoryItemId").type(JsonFieldType.STRING)
                .description("Item id in inventory servie"),
            fieldWithPath("createdAt").type("Date")
                .description("timestamp of resource created"),
            fieldWithPath("updatedAt").type("Date")
                .description("timestamp of resource updated")).and(descriptors);
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
  @WithMockUser(username = "catalog_ops", authorities = {SCOPE_CATALOG_READ, SCOPE_CATALOG_WRITE})
  public void testCreateAndGet() throws Exception {
    final String inventoryItemId = "12345X";

    final String imageALink = obtainLinkOfImage(imageAId);
    final String imageBLink = obtainLinkOfImage(imageBId);

    String postVariantResponse = mvc.perform(
        post(ENDPOINT).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
            .content(String.format(
                "{\"inventoryItemId\":\"%s\",\"images\":[\"%s\",\"%s\"]}",
                inventoryItemId, imageALink, imageBLink))).andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.inventoryItemId", is(inventoryItemId)))
        .andExpect(jsonPath("$._links.images.href", notNullValue()))
        .andExpect(jsonPath("$.createdAt", notNullValue()))
        .andExpect(jsonPath("$.updatedAt", notNullValue()))
        .andDo(document("productVariant/create", links(), requestFields(),
            responseFields(subsectionWithPath("_links").description("links to other resources"))))
        .andReturn().getResponse()
        .getContentAsString();

    String variantLink = objectMapper.readTree(postVariantResponse).at("/_links/self/href")
        .asText();

    String getVariantResponse = mvc.perform(
        get(variantLink).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.inventoryItemId", is(inventoryItemId)))
        .andExpect(jsonPath("$.createdAt", notNullValue()))
        .andExpect(jsonPath("$.updatedAt", notNullValue()))
        .andDo(document("productVariant/getOne", links(), responseFields(
            subsectionWithPath("_links")
                .description("common links to self and connected resources"))))
        .andReturn().getResponse()
        .getContentAsString();

    String imagesLink = objectMapper.readTree(getVariantResponse).at("/_links/images/href")
        .asText();

    mvc.perform(get(imagesLink).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andDo(print()).andExpect(jsonPath("$._embedded.productImages", hasSize(2)))
        .andDo(document("productVariant/getImages"));
  }

  @Test
  @WithMockUser(username = "catalog_ops", authorities = {SCOPE_CATALOG_READ, SCOPE_CATALOG_WRITE})
  public void testUpdate() throws Exception {
    ImmutableTriple<String, String, String> existedVariant = presetOneProductVariant();
    final String variantId = existedVariant.left;
    final String variantLink = existedVariant.middle;
    final String imagesLink = existedVariant.right;

    final String newInventoryItemId = "X123";
    final String imageCLink = obtainLinkOfImage(imageCId);

    mvc.perform(put(ENDPOINT + "/{id}", variantId).accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON).content(String
            .format("{\"inventoryItemId\":\"%s\"}", newInventoryItemId,
                imageCLink))).andExpect(status().isOk())
        .andExpect(jsonPath("$.inventoryItemId", is(newInventoryItemId)))
        .andDo(document("productVariant/update", links(), PayloadDocumentation.requestFields(
            fieldWithPath("inventoryItemId").type(JsonFieldType.STRING)
                .description("item id in inventory service")),
            responseFields(subsectionWithPath("_links").description("links to other resources"))));
  }

  @Test
  @WithMockUser(username = "catalog_ops", authorities = {SCOPE_CATALOG_READ, SCOPE_CATALOG_WRITE})
  public void testUpdateLinkedImages() throws Exception {
    ImmutableTriple<String, String, String> existedVariant = presetOneProductVariant();
    final String variantId = existedVariant.left;
    final String variantLink = existedVariant.middle;
    final String imagesLink = existedVariant.right;

    mvc.perform(get(imagesLink).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.productImages", hasSize(2)));

    final String imageCLink = obtainLinkOfImage(imageCId);

    mvc.perform(put(ENDPOINT + "/{productVariantId}/images", variantId)
        .contentType(MediaType.valueOf("text/uri-list")).content(imageCLink)).andDo(print())
        .andExpect(status().isNoContent()).andDo(document("productVariant/updateImages",
        pathParameters(parameterWithName("productVariantId").description("productVariant's id"))));

    mvc.perform(get(imagesLink).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.productImages", hasSize(1)));
  }

  @Test
  @WithMockUser(username = "catalog_ops", authorities = {SCOPE_CATALOG_READ, SCOPE_CATALOG_WRITE})
  public void testDelete() throws Exception {
    ImmutableTriple<String, String, String> existedVariant = presetOneProductVariant();
    final String variantId = existedVariant.left;
    final String variantLink = existedVariant.middle;
    final String imagesLink = existedVariant.right;

    mvc.perform(delete(ENDPOINT + "/{id}", variantId)).andExpect(status().isNoContent()).andDo(
        document("productVariant/delete",
            pathParameters(parameterWithName("id").description("productVariant's id"))));

    mvc.perform(get(ENDPOINT + "/{id}", variantId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  private String obtainLinkOfImage(final String imageId) throws Exception {
    String responseContent = mvc
        .perform(get("/productImages/{id}", imageId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    return objectMapper.readTree(responseContent).at("/_links/self/href").asText();
  }

  private ImmutableTriple<String, String, String> presetOneProductVariant() throws Exception {
    final String inventoryItemId = "12345X";

    final String imageALink = obtainLinkOfImage(imageAId);
    final String imageBLink = obtainLinkOfImage(imageBId);

    String postVariantResponse = mvc.perform(
        post(ENDPOINT).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
            .content(String.format(
                "{\"inventoryItemId\":\"%s\",\"images\":[\"%s\",\"%s\"]}",
                inventoryItemId, imageALink, imageBLink))).andExpect(status().isCreated())
        .andReturn().getResponse()
        .getContentAsString();

    String variantLink = objectMapper.readTree(postVariantResponse).at("/_links/self/href")
        .asText();
    String imagesLink = objectMapper.readTree(postVariantResponse).at("/_links/images/href")
        .asText();
    String variantId = Stream.of(variantLink.split("/")).reduce((first, second) -> second)
        .orElse(null);
    return ImmutableTriple.of(variantId, variantLink, imagesLink);
  }
}
