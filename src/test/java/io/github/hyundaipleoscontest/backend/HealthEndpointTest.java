package io.github.hyundaipleoscontest.backend;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.profiles.active=default")
class HealthEndpointTest {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ApplicationContext applicationContext;

  @Test
  void healthIsUpWithoutADatabase() {
    ResponseEntity<JsonNode> response =
        restTemplate.getForEntity("/actuator/health", JsonNode.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("status").asText()).isEqualTo("UP");
    assertThat(applicationContext.containsBean("dataSource")).isFalse();
  }
}
