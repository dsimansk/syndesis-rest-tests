package io.syndesis.qe.rest.endpoints;

import java.security.GeneralSecurityException;

import io.syndesis.model.connection.Connection;

/**
 * Connections rest client endpoint.
 *
 * @author jknetl
 */
public class ConnectionsEndpoint extends AbstractEndpoint<Connection> {
	public ConnectionsEndpoint(String syndesisUrl, String token) throws GeneralSecurityException {
		super(Connection.class, syndesisUrl, token, "connections");
	}
}
