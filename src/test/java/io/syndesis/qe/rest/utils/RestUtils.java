package io.syndesis.qe.rest.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import javax.ws.rs.client.Client;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import io.syndesis.qe.rest.exceptions.RestClientException;

/**
 * Utility class for Rest client (RestEasy).
 *
 * @author jknetl
 */
public final class RestUtils {

	private RestUtils() {
	}

	/**
	 * @return A REST client.
	 */
	public static Client getClient() throws RestClientException {
		ResteasyJackson2Provider jackson2Provider = RestUtils.createJacksonProvider();
		ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(RestUtils.createAllTrustingClient());

		Client client = new ResteasyClientBuilder()
				.providerFactory(new ResteasyProviderFactory()) // this is needed otherwise default jackson2provider is used, which causes problems with JDK8 Optional
				.register(jackson2Provider)
				.httpEngine(engine)
				.build();

		return client;
	}

	private static ResteasyJackson2Provider createJacksonProvider() {
		ResteasyJackson2Provider jackson2Provider = new ResteasyJackson2Provider();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new Jdk8Module());
		jackson2Provider.setMapper(objectMapper);
		return jackson2Provider;
	}

	//Required in order to skip certificate validation
	private static HttpClient createAllTrustingClient() throws RestClientException {
		HttpClient httpclient = null;
		try {
			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			});
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					builder.build());
			httpclient = HttpClients
					.custom()
					.setSSLSocketFactory(sslsf)
					.setMaxConnTotal(1000)
					.setMaxConnPerRoute(1000)
					.build();
		} catch (Exception e) {
			throw new RestClientException("Cannot create all SSL certificates trusting client", e);
		}
		return httpclient;
	}
}
