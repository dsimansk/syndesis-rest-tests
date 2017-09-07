package io.syndesis.qe.rest.endpoints;

import static io.restassured.RestAssured.given;

import lombok.extern.slf4j.Slf4j;

/**
 * TestSupport class contains utility methods for usage of the test-support endpoint.
 *
 * @author jknetl
 */
@Slf4j
public final class TestSupport {

	public static final String ENDPOINT_NAME = "test-support";

	private TestSupport() {
	}

	/**
	 * Resets syndesis database.
	 *
	 * @param token
	 */
	public static void resetDB(String token) {
		log.info("Resetting syndesis DB.");
		given().relaxedHTTPSValidation().auth().oauth2(token)
				.when()
				.get(ENDPOINT_NAME + "/reset-db")
				.asString();
	}
}
