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
	 * Test. GET /api/v1/connectorgroups/{id}
	 */
	@Ignore
	@Test
	public void connectorgroupsByIdTest() {

		assertTrue("Not implemented yet", false);
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
	 * Test. GET /api/v1/permissions/{id}
	 */
	@Ignore
	@Test
	public void permissionsByIdTest() {

		assertTrue("Not implemented yet", false);
	}

	/**
	 * Test. GET /api/v1/integrationtemplates/{id}
	 */
	@Test
	@Ignore("IntegrationTemplateEndpoint has been removed")
	public void integrationtemplatesTest() {

		final Response response = given().relaxedHTTPSValidation().auth().oauth2(token)
				.when()
				.get("/integrationtemplates");

		log.info("********** List integrationtemplates *********");
		log.info(response.asString());
		log.info("**********************************************");
		assertTrue(Integer.parseInt(response.path("totalCount").toString()) >= 0);
	}

	/**
	 * Test. GET /api/v1/integrationtemplates/{id}
	 */
	@Ignore
	@Test
	public void integrationtemplatesByIdTest() {

		assertTrue("Not implemented yet", false);
	}

	/**
	 * Test. POST /api/v1/integrationtemplates/{id}
	 */
	@Ignore
	@Test
	public void createIntegrationtemplatesByIdTest() {

		assertTrue("Not implemented yet", false);
	}

	/**
	 * Test. PUT /api/v1/integrationtemplates/{id}
	 */
	@Ignore
	@Test
	public void updateIntegrationtemplatesByIdTest() {

		assertTrue("Not implemented yet", false);
	}

	/**
	 * Test. DELETE /api/v1/integrationtemplates/{id}
	 */
	@Ignore
	@Test
	public void deleteIntegrationtemplatesByIdTest() {

		assertTrue("Not implemented yet", false);
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
	 * Test. GET /api/v1/users/{id}
	 */
	@Ignore
	@Test
	public void usersByIdTest() {

		assertTrue("Not implemented yet", false);
	}

	/**
	 * Test. GET /api/v1/actions
	 */
	@Test
	@Ignore("Action endpoint has been removed")
	public void actionsTest() {

		final Response response = given().relaxedHTTPSValidation().auth().oauth2(token)
				.when()
				.get("/actions");

		log.info("**************** List actions ****************");
		log.info(response.asString());
		log.info("**********************************************");
		assertTrue(Integer.parseInt(response.path("totalCount").toString()) >= 0);
	}

	/**
	 * Test. GET /api/v1/actions/{id}
	 */
	@Ignore
	@Test
	public void actionsByIdTest() {

		assertTrue("Not implemented yet", false);
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
	 * Test. GET /api/v1/roles/{id}
	 */
	@Ignore
	@Test
	public void rolesByIdTest() {

		assertTrue("Not implemented yet", false);
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

	/**
	 * Test. GET /api/v1/integrationpatterns/{id}
	 */
	@Test
	@Ignore("IntegrationPatternEndpoint has been removed")
	public void integrationpatternsTest() {

		final Response response = given().relaxedHTTPSValidation().auth().oauth2(token)
				.when()
				.get("/integrationpatterns");

		log.info("*********** List integrationpatterns *********");
		log.info(response.asString());
		log.info("**********************************************");
		assertTrue(Integer.parseInt(response.path("totalCount").toString()) >= 0);
	}

	/**
	 * Test. GET /api/v1/users/{id}
	 */
	@Ignore
	@Test
	public void integrationpatternsByIdTest() {

		assertTrue("Not implemented yet", false);
	}

	@Test
	@Ignore("IntegrationTemplateEndpoint has been removed")
	public void integrationTemplatesTest() {

		final Response response = given().relaxedHTTPSValidation().auth().oauth2(token)
				.when()
				.get("/integrationtemplates");

		log.info("********** List integrationTemplates *********");
		log.info(response.asString());
		log.info("**********************************************");

		assertTrue(Integer.parseInt(response.path("totalCount").toString()) >= 0);
	}

	/**
	 * Test. POST /api/v1/integration-support/generate/pom.xml
	 */
	@Ignore
	@Test
	public void integrationsupportTest() {

		assertTrue("Not implemented yet", false);
	}

	/**
	 * Test. POST /api/v1/event/reservations
	 */
	@Ignore
	@Test
	public void eventReservationsTest() {

		assertTrue("Not implemented yet", false);
	}
}
