package com.github.mmichaelis.testcontainers;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URL;

import static com.github.mmichaelis.testcontainers.TestcontainersUtil.readFromUrl;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests direct support of {@code Dockerfile} from within tests, when there
 * are no ready-made docker images available.
 *
 * @see <a href="https://www.testcontainers.org/features/creating_images/">Creating images on-the-fly - Testcontainers</a>
 * @see <a href="https://www.baeldung.com/docker-test-containers">Docker Test Containers in Java Tests | Baeldung</a>
 */
@DefaultAnnotation(NonNull.class)
class DockerfileSupportTest {
  @Nested
  @Testcontainers
  class ViaDockerfile {
    @Container
    private GenericContainer dockerfileContainer = new GenericContainer(
            new ImageFromDockerfile()
                    .withFileFromClasspath("Dockerfile", "simplewebserver/Dockerfile")
    )
            .withExposedPorts(80);

    @Test
    void proofOfConcept() throws Exception {
      String address = "http://" + dockerfileContainer.getContainerIpAddress() + ":" + dockerfileContainer.getFirstMappedPort();
      String response = readFromUrl(new URL(address));

      assertThat(response).isEqualTo("Hello World!");
    }
  }

  @Nested
  @Testcontainers
  class ViaDockerfileDsl {
    @Container
    private GenericContainer dockerfileContainer = new GenericContainer(
            new ImageFromDockerfile()
                    .withDockerfileFromBuilder(builder ->
                            builder
                                    .from("alpine:latest")
                                    .cmd("/bin/sh", "-c", "while true; do echo 'HTTP/1.1 200 OK\n\nHello World!' | nc -l -p 80; done")
                                    .expose(80)
                                    .build()
                    )
    )
            .withExposedPorts(80);

    @Test
    void proofOfConcept() throws Exception {
      String address = "http://" + dockerfileContainer.getContainerIpAddress() + ":" + dockerfileContainer.getFirstMappedPort();
      String response = readFromUrl(new URL(address));

      assertThat(response).isEqualTo("Hello World!");
    }
  }
}
