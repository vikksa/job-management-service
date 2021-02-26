package com.learn.optile;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base Setup Class for all test cases
 */

@DisplayName("Base test specifications.")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
abstract class AbstractBaseSpec {

    @LocalServerPort
    private int localServerPort;

    @BeforeEach
    public void setup() {
        RestAssured.port = localServerPort;
    }

}
