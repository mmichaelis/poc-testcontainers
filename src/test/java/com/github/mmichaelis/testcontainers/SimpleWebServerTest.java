package com.github.mmichaelis.testcontainers;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URL;

import static com.github.mmichaelis.testcontainers.TestcontainersUtil.readFromUrl;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple Web Server test provided by Baeldung.
 *
 * @see <a href="https://www.baeldung.com/docker-test-containers">Docker Test Containers in Java Tests | Baeldung</a>
 */
@Testcontainers
@DefaultAnnotation(NonNull.class)
class SimpleWebServerTest {
  private static final String SERVICE_NAME = "simpleWebServer_1";
  private static final int SERVICE_PORT = 80;

  @Container
  private static final DockerComposeContainer COMPOSE_CONTAINER =
          new DockerComposeContainer(TestcontainersUtil.getResourceAsFile("simplewebserver.docker-compose.yml"))
                  .withLocalCompose(true)
                  .withExposedService(SERVICE_NAME, SERVICE_PORT);

  @Test
  void proofOfConcept() throws Exception {
    String address = "http://" + COMPOSE_CONTAINER.getServiceHost(SERVICE_NAME, SERVICE_PORT) + ":" + COMPOSE_CONTAINER.getServicePort(SERVICE_NAME, SERVICE_PORT);
    String response = readFromUrl(new URL(address));

    assertThat(response).isEqualTo("Hello World!");
  }
}
