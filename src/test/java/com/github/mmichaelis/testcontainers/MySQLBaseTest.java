package com.github.mmichaelis.testcontainers;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static java.lang.invoke.MethodHandles.lookup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

@Testcontainers
@DefaultAnnotation(NonNull.class)
class MySQLBaseTest {
  private static final Logger LOG = getLogger(lookup().lookupClass());
  private static final Logger MYSQL_LOG = getLogger("mysql");

  @Container
  private static final MySQLContainer MY_SQL_CONTAINER = (MySQLContainer) new MySQLContainer().withLogConsumer(new Slf4jLogConsumer(MYSQL_LOG));
  private static Properties jdbcProperties;

  @BeforeAll
  static void setUpAll() {
    jdbcProperties = new Properties();
    jdbcProperties.setProperty("user", MY_SQL_CONTAINER.getUsername());
    jdbcProperties.setProperty("password", MY_SQL_CONTAINER.getPassword());
  }

  @Test
  void shouldBeRunning() {
    assertThat(MY_SQL_CONTAINER.isRunning()).isTrue();

    LOG.info("MySQL available at: {}", MY_SQL_CONTAINER.getJdbcUrl());
    LOG.info(" Driver Class Name: {}", MY_SQL_CONTAINER.getDriverClassName());
    LOG.info("     Database Name: {}", MY_SQL_CONTAINER.getDatabaseName());
    LOG.info(" Test Query String: {}", MY_SQL_CONTAINER.getTestQueryString());
    LOG.info("          Username: {}", MY_SQL_CONTAINER.getUsername());
    LOG.info("      Container ID: {}", MY_SQL_CONTAINER.getContainerId());
    LOG.info(" Docker Image Name: {}", MY_SQL_CONTAINER.getDockerImageName());
  }

  @Test
  void accessMySqlDatabase() throws SQLException {
    Driver driver = MY_SQL_CONTAINER.getJdbcDriverInstance();
    try (Connection connection = driver.connect(MY_SQL_CONTAINER.getJdbcUrl(), jdbcProperties)) {
      DatabaseMetaData metaData = connection.getMetaData();
      ResultSet catalogs = metaData.getCatalogs();
      while (catalogs.next()) {
        LOG.info("Catalog: {}", catalogs.getString("TABLE_CAT"));
      }

      ResultSet tables = metaData.getTables(null, null, "%", null);
      while (tables.next()) {
        LOG.info("Table: {}", tables.getString("TABLE_NAME"));
      }
    }
  }
}
