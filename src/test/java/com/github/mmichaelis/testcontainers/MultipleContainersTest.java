package com.github.mmichaelis.testcontainers;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;

import static java.lang.invoke.MethodHandles.lookup;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

@Testcontainers
@DefaultAnnotation(NonNull.class)
class MultipleContainersTest {

  private static final String SERVICE_NAME = "wordpress_main";
  private static final int SERVICE_PORT = 80;
  private static final Logger SERVICE_LOG = getLogger(SERVICE_NAME);

  @Container
  private static final DockerComposeContainer COMPOSE_CONTAINER =
          new DockerComposeContainer(getDockerComposeFile("wordpress.docker-compose.yml"))
                  .withExposedService(SERVICE_NAME, SERVICE_PORT, Wait.forHttp("/").withStartupTimeout(Duration.ofMinutes(2)))
                  .withLogConsumer(SERVICE_NAME, new Slf4jLogConsumer(SERVICE_LOG));

  private static URL serviceUrl;

  private static File getDockerComposeFile(String name) {
    URL resource = requireNonNull(lookup().lookupClass().getResource(name), "Cannot find '" + name + "'.");
    try {
      URI uri = resource.toURI();
      return new File(uri);
    } catch (URISyntaxException e) {
      throw new RuntimeException("URI invalid.", e);
    }
  }

  @BeforeAll
  static void setUpAll() throws MalformedURLException {
    String serviceHost = COMPOSE_CONTAINER.getServiceHost(SERVICE_NAME, SERVICE_PORT);
    int servicePort = COMPOSE_CONTAINER.getServicePort(SERVICE_NAME, SERVICE_PORT);
    serviceUrl = URI.create(String.format("http://%s:%d/", serviceHost, servicePort)).toURL();
  }

  @Test
  void shouldBeStarted() throws IOException {
    HttpURLConnection connection = (HttpURLConnection) serviceUrl.openConnection();
    try {
      int responseCode = connection.getResponseCode();
      assertThat(responseCode).isEqualTo(200);
      String page = readPage(connection);
      assertThat(page).isNotEmpty();
    } finally {
      connection.disconnect();
    }
  }

  private static String readPage(URLConnection connection) throws IOException {
    try (BufferedReader in = new BufferedReader(
            new InputStreamReader(connection.getInputStream()))) {
      String inputLine;
      StringBuilder content = new StringBuilder();
      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine);
      }
      return content.toString();
    }
  }
}
