package io.syndesis.qe.rest.tests.simple;

import static org.junit.Assert.assertTrue;

import static org.hamcrest.Matchers.equalTo;

import static io.restassured.RestAssured.given;

import io.syndesis.qe.rest.tests.AbstractSyndesisRestTest;
import io.syndesis.qe.rest.utils.SampleDataGenerator;
import io.syndesis.qe.rest.endpoints.TestSupport;

import org.junit.After;
import org.junit.Test;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.restassured.response.Response;
import io.syndesis.model.integration.Integration;
import lombok.extern.slf4j.Slf4j;

/**
 * Integrations crud operations test.
 *
 * Jun 15, 2017 Red Hat
 *
 * @author tplevko@redhat.com
 */
@Slf4j
public class IntegrationsCrudTest extends AbstractSyndesisRestTest {

	@After
	public void cleanUp() {
		TestSupport.resetDB(syndesisToken);
	}

	/**
	 * Test. POST /api/v1/integrations Test. GET /api/v1/integrations/{id}
	 */
	@Test
	public void createFindIntegrationTest() {

		final String integrationName = "Test";
		final Response integration = createSampleIntegration(integrationName);

		assertTrue(findIntegrationById(integration.path("id").toString()).path("name").equals(integrationName));
	}

	/**
	 * Test. GET /api/v1/integrations
	 */
	@Test
	public void listIntegrationsTest() {

		final String integrationName = "Test";
		final Response integrationResponse = createSampleIntegration(integrationName);

		final List<String> integrationNames = new ArrayList<>();
		final Response integrations = given().relaxedHTTPSValidation().auth().oauth2(syndesisToken)
				.when()
				.get("/integrations");

		final JSONObject jsonObject = new JSONObject(integrations.asString());
		final int totalCount = jsonObject.getInt("totalCount");

		for (int i = 0; i < totalCount; i++) {
			final String name = jsonObject.getJSONArray("items").getJSONObject(i).getString("name");
			log.info(name);
			integrationNames.add(name);
		}

		assertTrue(integrationNames.contains("Test"));
	}

	/**
	 * Test. PUT /api/v1/integrations/{id}
	 */
	@Test
	public void updateIntegrationTest() {

		final String integrationName = "Test";
		final Response integrationResponse = createSampleIntegration(integrationName);

		final String newIntegrationName = "Updated";
		final Integration testIntegration = SampleDataGenerator.createSampleIntegration(newIntegrationName,
				Optional.of(integrationResponse.path("id").toString()), Optional.of(syndesisToken));

		given().relaxedHTTPSValidation().auth().oauth2(syndesisToken).request()
				.contentType("application/json").body(testIntegration)
				.pathParam("id", integrationResponse.path("id").toString())
				.then().expect().response().statusCode(204)
				.when().put("/integrations/{id}");

		final Response response = findIntegrationById(integrationResponse.path("id").toString());
		final String name = response.path("name");
		assertTrue(name.endsWith(newIntegrationName));
	}

	/**
	 * Test. DELETE /api/v1/integrations/{id}
	 */
	@Test
	public void deleteIntegrationTest() {

		final String integrationName = "Test";
		final Response integrationResponse = createSampleIntegration(integrationName);

		given().relaxedHTTPSValidation().auth().oauth2(syndesisToken).request()
				.pathParam("id", integrationResponse.path("id").toString())
				.then().expect().response().statusCode(204).when().delete("/integrations/{id}");

		final Response response = findIntegrationById(integrationResponse.path("id").toString());
		final String developerMsg = response.path("developerMsg");

		assertTrue(developerMsg.endsWith("Entity Not Found Exception null"));
	}

	private Response findIntegrationById(String id) {

		final Response response = given().relaxedHTTPSValidation().auth().oauth2(syndesisToken).request()
				.pathParam("id", id)
				.then().expect().response()
				.when().get("/integrations/{id}");

		return response;
	}

	private Response createSampleIntegration(String name) {

		final String integrationName = "Test";
		final Integration testIntegration = SampleDataGenerator.createSampleIntegration(integrationName);

		final Response integration = given().relaxedHTTPSValidation().auth().oauth2(syndesisToken)
				.request().contentType("application/json").body(testIntegration)
				.then().expect().response().body("name", equalTo(integrationName))
				.when().post("/integrations");

		return integration;
	}
}
