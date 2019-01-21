package com.github.mmichaelis.testcontainers;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

@Testcontainers
@DefaultAnnotation(NonNull.class)
class WordPressTest {
  private static final String WORDPRESS_NAME = "wordpress_1";
  private static final int WORDPRESS_PORT = 80;
  private static final Logger WORDPRESS_LOG = getLogger("wordpress");

  private static final String MYSQL_NAME = "db_1";
  private static final Logger MYSQL_LOG = getLogger("mysql");

  @Container
  private static final DockerComposeContainer COMPOSE_CONTAINER =
          new DockerComposeContainer(TestcontainersUtil.getResourceAsFile("wordpress.docker-compose.yml"))
                  .withLocalCompose(true)
                  .withExposedService(WORDPRESS_NAME, WORDPRESS_PORT)
                  .withLogConsumer(WORDPRESS_NAME, new Slf4jLogConsumer(WORDPRESS_LOG))
                  .withLogConsumer(MYSQL_NAME, new Slf4jLogConsumer(MYSQL_LOG))
          ;

  private URL serviceUrl;

  @BeforeEach
  void setUpEach() throws MalformedURLException {
    String serviceHost = COMPOSE_CONTAINER.getServiceHost(WORDPRESS_NAME, WORDPRESS_PORT);
    int servicePort = COMPOSE_CONTAINER.getServicePort(WORDPRESS_NAME, WORDPRESS_PORT);
    serviceUrl = URI.create(String.format("http://%s:%d/", serviceHost, servicePort)).toURL();
  }

  @Test
  void shouldBeStarted() throws IOException {
    assertThat(TestcontainersUtil.readFromUrl(serviceUrl))
            .contains("<title>WordPress")
            .contains("Select a default language");
  }

}
