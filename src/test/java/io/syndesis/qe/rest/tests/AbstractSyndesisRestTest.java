package io.syndesis.qe.rest.tests;

import io.syndesis.qe.rest.utils.SyndesisRestConstants;

import io.restassured.RestAssured;

/**
 * Abstract base for syndesis rest tests.
 *
 * Jun 26, 2017 Red Hat
 *
 * @author tplevko@redhat.com
 */
public abstract class AbstractSyndesisRestTest {

	protected static final String token = System.getProperty(SyndesisRestConstants.TOKEN);
	protected static final String syndesisURL = System.getProperty(SyndesisRestConstants.SYNDESIS_URL);

	public AbstractSyndesisRestTest() {
		RestAssured.baseURI = syndesisURL;
		RestAssured.basePath = SyndesisRestConstants.API_PATH;
	}
}
