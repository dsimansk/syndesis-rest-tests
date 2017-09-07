package io.syndesis.qe.rest.tests.simple;

import static org.junit.Assert.assertTrue;

import static io.restassured.RestAssured.given;

import io.syndesis.qe.rest.tests.AbstractSyndesisRestTest;
import io.syndesis.qe.rest.endpoints.TestSupport;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Connectors tests. Many of the endpoints don't return anything, will update the tests when there is something to test
 * with them.
 *
 * Jul 10, 2017 Red Hat
 *
 * @author tplevko@redhat.com
 */
@Slf4j
public class ConnectorsTest extends AbstractSyndesisRestTest {

	@AfterClass
	public static void cleanUp() {
		TestSupport.resetDB(token);
	}

	@Test
	public void getConnectorsActions() {

		final List<String> connectorIds = new ArrayList<>();

		final Response result = given().relaxedHTTPSValidation().auth().oauth2(token)
				.when()
				//				.pathParam("id", "twitter")
				//				.get("/connectors/{id}/actions");
				.get("/connectors");

		final JSONObject jsonObject = new JSONObject(result.asString());

		final int totalCount = jsonObject.getInt("totalCount");

		for (int i = 0; i < totalCount; i++) {
			final String integrationName = jsonObject.getJSONArray("items").getJSONObject(i).getString("id");
			log.info(integrationName);
			connectorIds.add(integrationName);
		}

		for (String connectorId : connectorIds) {

			final Response response = given().relaxedHTTPSValidation().auth().oauth2(token)
					.when()
					.pathParam("id", connectorId)
					.get("/connectors/{id}/actions");

			log.info("********* " + connectorId + " List actions ***************");
			log.info(response.asString());
			log.info("**********************************************");

			// at this moment, there are no actions returned for any of the connectors, IDKW
			assertTrue(Integer.parseInt(response.path("totalCount").toString()) >= 0);
		}
	}

	/**
	 * Test. GET /api/v1/connectors
	 */
	@Test
	public void getConnectorsListTest() {

		final Response response = given().relaxedHTTPSValidation().auth().oauth2(token)
				.when()
				.get("/connectors");

		//TODO(tplevko): add some more sofisticated test
		assertTrue(Integer.parseInt(response.path("totalCount").toString()) >= 0);
	}

	/**
	 * Test. GET /api/v1/connectors/{id}
	 */
	@Test
	public void ftpConnectorsByIdTest() {

		final Response response = given().relaxedHTTPSValidation().auth().oauth2(token)
				.when()
				.pathParam("id", "ftp")
				.get("/connectors/{id}");

		log.info("************** ftp  connector ****************");
		log.info(response.asString());
		log.info("**********************************************");
		assertTrue(response.path("name").toString().endsWith("File Transfer"));
	}

	/**
	 * Test. GET /api/v1/connectors/{id}/actions/{id}
	 */
	@Ignore
	@Test
	public void connectorsByIdActionsByIdTest() {

		assertTrue("Not implemented yet", false);
	}

	/**
	 * Test. POST /api/v1/connectors/{id}/verifier
	 */
	@Ignore
	@Test
	public void connectorsByIdVerifierTest() {

		assertTrue("Not implemented yet", false);
	}
}
