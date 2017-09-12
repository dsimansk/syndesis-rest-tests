package io.syndesis.qe.rest.tests.simple;

import static org.junit.Assert.assertTrue;

import static io.restassured.RestAssured.given;

import io.syndesis.qe.rest.tests.AbstractSyndesisRestTest;

import org.junit.Ignore;
import org.junit.Test;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * These endpoints don't do anything much useful for now. This class aggregates them mainly for tracking purposes.
 *
 * Jun 15, 2017 Red Hat
 *
 * @author tplevko@redhat.com
 */
@Slf4j
public class DummyEndpointsTest extends AbstractSyndesisRestTest {

	/**
	 * Test. GET /api/v1/connectorgroups
	 */
	@Test
	public void connectorgroupsTest() {

		final Response response = given().relaxedHTTPSValidation().auth().oauth2(token)
				.when()
				.get("/connectorgroups");

		log.info("************ List connectorgroups ************");
		log.info(response.asString());
		log.info("**********************************************");
		assertTrue(Integer.parseInt(response.path("totalCount").toString()) >= 0);
	}


	/**
	 * Test. GET /api/v1/permissions
	 */
	@Test
	public void permissionsTest() {

		final Response response = given().relaxedHTTPSValidation().auth().oauth2(token)
				.when()
				.get("/permissions");

		log.info("************** List permissions **************");
		log.info(response.asString());
		log.info("**********************************************");
		assertTrue(Integer.parseInt(response.path("totalCount").toString()) >= 0);
	}

	/**
	 * Test. GET /api/v1/users
	 */
	@Test
	public void usersTest() {

		final Response response = given().relaxedHTTPSValidation().auth().oauth2(token)
				.when()
				.get("/users");

		log.info("**************** List users ******************");
		log.info(response.asString());
		log.info("**********************************************");
		assertTrue(Integer.parseInt(response.path("totalCount").toString()) >= 0);
	}



	/**
	 * Test. GET /api/v1/roles
	 */
	@Test
	public void rolesTest() {

		final Response response = given().relaxedHTTPSValidation().auth().oauth2(token)
				.when()
				.get("/roles");

		log.info("**************** List roles ******************");
		log.info(response.asString());
		log.info("**********************************************");
		assertTrue(Integer.parseInt(response.path("totalCount").toString()) >= 0);
	}

	/**
	 * Test. GET /api/v1/tags
	 */
	@Test
	public void tagsTest() {

		final Response response = given().relaxedHTTPSValidation().auth().oauth2(token)
				.when()
				.get("/tags");

		log.info("**************** List tags *******************");
		log.info(response.asString());
		log.info("**********************************************");
		assertTrue(Integer.parseInt(response.path("totalCount").toString()) >= 0);
	}
}
