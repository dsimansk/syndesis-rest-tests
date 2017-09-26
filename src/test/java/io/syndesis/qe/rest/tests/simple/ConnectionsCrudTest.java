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
import io.syndesis.model.connection.Connection;
import lombok.extern.slf4j.Slf4j;

/**
 * Connections crud operations test.
 *
 * Jun 15, 2017 Red Hat
 *
 * @author tplevko@redhat.com
 */
@Slf4j
public class ConnectionsCrudTest extends AbstractSyndesisRestTest {

	@After
	public void cleanUp() {
		TestSupport.resetDB(syndesisToken);
	}

	/**
	 * Test. POST /api/v1/connections Test. GET /api/v1/connections/{id}
	 */
	@Test
	public void createFindConnectionTest() {

		final String testConnectionName = "Test";
		final Response result = createSalesforceConnection(testConnectionName);

		assertTrue(findConnectionById(result.path("id").toString()).path("name").equals(testConnectionName));
	}

	/**
	 * Test. GET /api/v1/connections
	 */
	@Test
	public void listConnectionsTest() {

		final List<String> connectionsNames = new ArrayList<>();
		final String testConnectionName = "Test";
		final Response connectionResponse = createSalesforceConnection(testConnectionName);

		final Response result = given().relaxedHTTPSValidation().auth().oauth2(syndesisToken)
				.when()
				.get("/connections");

		final JSONObject jsonObject = new JSONObject(result.asString());
		final int totalCount = jsonObject.getInt("totalCount");

		for (int i = 0; i < totalCount; i++) {
			final String integrationName = jsonObject.getJSONArray("items").getJSONObject(i).getString("name");
			log.info(integrationName);
			connectionsNames.add(integrationName);
		}

		assertTrue(connectionsNames.contains(testConnectionName));
	}

	/**
	 * Test. PUT /api/v1/connections/{id}
	 */
	@Test
	public void updateConnectionTest() {

		final String testConnectionName = "Test";
		final String newConnectionName = "Updated";
		final Response result = createSalesforceConnection(testConnectionName);

		final Connection connection = SampleDataGenerator.createSampleSalesforceConnection(newConnectionName, Optional.of(result.path("id").toString()));

		given().relaxedHTTPSValidation().auth().oauth2(syndesisToken).request()
				.contentType("application/json").body(connection)
				.pathParam("id", result.path("id").toString())
				.then().expect().response().statusCode(204)
				.when().put("/connections/{id}");

		assertTrue(findConnectionById(result.path("id").toString()).path("name").equals(newConnectionName));
	}

	/**
	 * Test. DELETE /api/v1/connections{id}
	 */
	@Test
	public void deleteConnectionTest() {

		final String testConnectionName = "Test";
		final Response result = createSalesforceConnection(testConnectionName);

		given().relaxedHTTPSValidation().auth().oauth2(syndesisToken).request()
				.pathParam("id", result.path("id").toString())
				.then().expect().response().statusCode(204).when().delete("/connections/{id}");

		final Response response = findConnectionById(result.path("id").toString());
		final String developerMsg = response.path("developerMsg");

		assertTrue(developerMsg.endsWith("Entity Not Found Exception null"));
		log.debug(developerMsg);
	}

	private Response findConnectionById(String id) {

		final Response response = given().relaxedHTTPSValidation().auth().oauth2(syndesisToken).request()
				.pathParam("id", id)
				.then().expect().response()
				.when().get("/connections/{id}");

		log.debug(response.asString());

		return response;
	}

	private Response createSalesforceConnection(String name) {

		final Connection connection = SampleDataGenerator.createSampleSalesforceConnection(name);
		final Response result = given().relaxedHTTPSValidation().auth().oauth2(syndesisToken)
				.request().contentType("application/json").body(connection)
				.then().expect().response()
				.body("name", equalTo(name))
				.when().post("/connections");
		return result;
	}
}
