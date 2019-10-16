package io.github.rscai.microservices.catalog;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

@Configuration
public class GlobalRepositoryRestConfigurer implements RepositoryRestConfigurer {

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
    config.getCorsRegistry().addMapping("/**")
        .allowedOrigins("*").allowedHeaders("*").allowedMethods("*");
  }
}