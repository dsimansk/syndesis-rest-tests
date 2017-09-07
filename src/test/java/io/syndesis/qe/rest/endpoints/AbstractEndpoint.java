package io.syndesis.qe.rest.endpoints;

import io.syndesis.qe.rest.utils.RestUtils;
import io.syndesis.qe.rest.utils.SyndesisRestConstants;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.security.GeneralSecurityException;
import java.util.List;

import io.syndesis.model.ListResult;
import io.syndesis.model.WithName;

/**
 * Implements a client endpoint for syndesis REST.
 *
 * @author jknetl
 */
public abstract class AbstractEndpoint<T extends WithName> {

	protected String endpointName;
	protected String syndesisUrl;
	protected String token;
	protected String apiPath = SyndesisRestConstants.API_PATH;
	private Class<T> type;
	private final Client client;

	public AbstractEndpoint(Class<?> type, String syndesisUrl, String token, String endpointName) throws GeneralSecurityException {
		this.type = (Class<T>) type;
		this.syndesisUrl = syndesisUrl;
		this.token = token;
		this.endpointName = endpointName;

		client = RestUtils.getClient();
	}

	public T create(T obj) {
		Invocation.Builder invocation = client
				.target(getEndpointUrl())
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token);

		Response response = invocation.post(Entity.entity(obj, MediaType.APPLICATION_JSON));

		return response.readEntity(type);
	}

	public void delete(String id) {
		Invocation.Builder invocation = client
				.target(getEndpointUrl() + "/" + id)
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token);

		Response response = invocation.delete();
	}

	public T get(String id) {
		Invocation.Builder invocation = client
				.target(getEndpointUrl() + "/" + id)
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token);
		Response response = invocation
				.get();

		T result = response.readEntity(type);

		return result;
	}

	public List<T> list() {
		Invocation.Builder invocation = client
				.target(getEndpointUrl())
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token);
		Response response = invocation
				.get();

		ListResult<T> ts = response.readEntity(new GenericType<ListResult<T>>() {});

		return ts.getItems();
	}

	public String getEndpointUrl() {
		return String.format("%s%s%s", syndesisUrl, apiPath, endpointName);
	}
}
