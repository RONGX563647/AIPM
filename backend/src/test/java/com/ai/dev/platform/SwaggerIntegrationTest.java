package com.ai.dev.platform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "jwt.secret=test-swagger"
})
public class SwaggerIntegrationTest {
    @Autowired
    TestRestTemplate rest;

    @Test
    public void apiDocsExposeAuthPaths() {
        ResponseEntity<String> r = rest.getForEntity("/v3/api-docs", String.class);
        assertEquals(200, r.getStatusCodeValue());
        assertTrue(r.getBody() != null && r.getBody().contains("/sys/user/login"));
    }
}
