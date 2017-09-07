package io.syndesis.qe.rest.endpoints;

import java.security.GeneralSecurityException;

import io.syndesis.model.integration.Integration;

/**
 * Integrations client endpoint.
 *
 * @author jknetl
 */
public class IntegrationsEndpoint extends AbstractEndpoint<Integration> {
	public IntegrationsEndpoint(String syndesisUrl, String token) throws GeneralSecurityException {
		super(Integration.class, syndesisUrl, token, "integrations");
	}
}
