package com.github.mmichaelis.testcontainers;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DefaultAnnotation(NonNull.class)
class MySQLBaseTest {
  @Container
  private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer();

  @Test
  void shouldBeRunning() {
    Assertions.assertThat(MY_SQL_CONTAINER.isRunning()).isTrue();
  }
}
