package io.syndesis.qe.rest.tests;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
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

	protected static final String syndesisToken = SyndesisRestConstants.SYNDESIS_TOKEN;
	protected static final String syndesisURL = SyndesisRestConstants.SYNDESIS_URL;
	protected static final String openshiftToken = SyndesisRestConstants.OPENSHIFT_TOKEN;
	protected static final String openshiftUrl = SyndesisRestConstants.OPENSHIFT_URL;
	protected static OpenShiftClient openshiftClient;

	public AbstractSyndesisRestTest() {
		RestAssured.baseURI = syndesisURL;
		RestAssured.basePath = SyndesisRestConstants.API_PATH;

		Config config = new ConfigBuilder()
				.withOauthToken(openshiftToken)
				.withMasterUrl(openshiftUrl)
				.build();
		openshiftClient = new DefaultOpenShiftClient(config);
	}
}
