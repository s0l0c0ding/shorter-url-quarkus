package dev.solocoding.contract;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class OpenApiContractIntegrationTest {

    @Test
    // AC-3.10
    void shouldExposePublicUrlSecurityAndErrorCodesInOpenApiContract() throws Exception {
        Path contract = Path.of("target/test-openapi/openapi.yaml");
        if (!Files.exists(contract)) {
            contract = Path.of("docs/openapi.yaml");
        }

        String yaml = Files.readString(contract);

        assertTrue(yaml.contains("/public/url"));
        assertTrue(yaml.contains("scheme: basic"));
        assertTrue(yaml.contains("NOT_VALID_EXPIRATION"));
        assertTrue(yaml.contains("RATE_LIMITED"));
        assertTrue(yaml.contains("RATE_LIMIT_UNAVAILABLE"));
    }
}
