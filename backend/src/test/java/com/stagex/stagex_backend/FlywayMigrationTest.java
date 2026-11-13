package com.stagex.stagex_backend;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.datasource.url=${STAGEX_TEST_DATABASE_URL}")
@ActiveProfiles("local")
@EnabledIfEnvironmentVariable(named = "STAGEX_TEST_DATABASE_URL", matches = ".+")
class FlywayMigrationTest {

    @Autowired
    private Flyway flyway;

    @Test
    void initialMigrationIsAppliedOnStartup() {
        flyway.validate();
        assertTrue(java.util.Arrays.stream(flyway.info().applied())
                .anyMatch(migration -> "V1__initial_schema.sql".equals(migration.getScript())));
    }
}
