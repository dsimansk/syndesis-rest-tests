package io.syndesis.qe.rest.utils;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.FederatedIdentityRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.syndesis.qe.rest.accounts.Account;
import io.syndesis.qe.rest.accounts.AccountsDirectory;
import lombok.extern.slf4j.Slf4j;

/**
 * Keycloak utils class.
 *
 * Sep 8, 2017 Red Hat
 *
 * @author tplevko@redhat.com
 */
@Slf4j
public final class KeycloakUtils {

	private static String keycloakUrl;
	private static Account keycloakAccount;
	private static String keycloakAdminName;
	private static String keycloakAdminPass;
	private static Client client;

	private static Keycloak kc;
	private static KeycloakUtils keycloakUtils = null;

	public static KeycloakUtils getInstance() {
		if (keycloakUtils == null) {
			keycloakUtils = new KeycloakUtils();
		}
		return keycloakUtils;
	}

	private KeycloakUtils() {
		final String masterRealmName = "master";
		final AccountsDirectory accountsDirectory = new AccountsDirectory(Paths.get(SyndesisRestConstants.ACCOUNT_CONFIG_PATH));

		keycloakAccount = accountsDirectory.getAccount("keycloak").get();

		keycloakUrl = keycloakAccount.getProperty("instanceUrl");
		keycloakAdminName = keycloakAccount.getProperty("adminLogin");
		keycloakAdminPass = keycloakAccount.getProperty("adminPassword");

		client = RestUtils.getClient();

		this.kc = KeycloakBuilder.builder()
				.serverUrl(keycloakUrl)
				.realm(masterRealmName)
				.username(keycloakAdminName)
				.password(keycloakAdminPass)
				.clientId("admin-cli")
				.resteasyClient(
						// solve the issue with generated SSL certificate - the client won't complain about it anymore.
						new ResteasyClientBuilder().disableTrustManager()
						.connectionPoolSize(10).build()
				).build();
		getAdminToken();
		//increase amdin token lifespan
		setSyndesisAccessTokenLifespan(masterRealmName, 18000);
		//increase syndesis token lifespan
		setSyndesisAccessTokenLifespan(keycloakAccount.getProperty("syndesisRealmName"), 18000);
	}

	/**
	 * Gets the kc admin token.
	 *
	 * @return
	 */
	public static String getAdminToken() {

		final TokenManager tokenManager = kc.tokenManager();
		final String token = tokenManager.getAccessToken().getToken();
		kc.tokenManager().grantToken();
		return token;
	}

	/**
	 * Gets the user token.
	 *
	 * @param userName
	 * @param userPassword
	 * @param syndesisclientId
	 * @param syndesisRealm
	 * @return
	 */
	public static String getUserToken(String userTokenUrl, String userName, String userPassword, String syndesisclientId, String syndesisRealm) {

		final Keycloak userKc = KeycloakBuilder.builder()
				.serverUrl(userTokenUrl)
				.realm(syndesisRealm)
				.username(userName)
				.password(userPassword)
				.clientId(syndesisclientId)
				.resteasyClient(
						// solve the issue with generated SSL certificate - the client won't complain about it anymore.
						new ResteasyClientBuilder().disableTrustManager()
						.connectionPoolSize(10).build()
				).build();
		userKc.tokenManager().grantToken();
		final TokenManager tokenManager = userKc.tokenManager();
		final String token = tokenManager.getAccessToken().getToken();

		log.debug("*****************");
		log.debug(token);
		log.debug("*****************");

		return token;
	}

	public static void getSyndesisUiClientConf(String syndesisRealmName, String syndesisClientName) {

		final List<ClientRepresentation> clients = kc.realm(syndesisRealmName).clients().findAll();

		for (ClientRepresentation c : clients) {
			log.info(c.getName());
			if (c.getClientId().equals(syndesisClientName)) {
				c.setDirectAccessGrantsEnabled(Boolean.TRUE);
				kc.realm("syndesis").clients().create(c);
			}
		}
	}

	/**
	 * checks if user with specified user name exists in specified real.
	 *
	 * @param syndesisRealm
	 * @param userName
	 * @return
	 */
	public static boolean checkIfUserExists(String syndesisRealm, String userName) {

		boolean exists = false;
		if (kc.realm(syndesisRealm).users().search(userName, 0, 1).size() > 0) {
			exists = true;
		}
		return exists;
	}

	/**
	 * Edit syndesis-ui client configuration to accept "Direct access" using user password. Necessary so we can later
	 * get user token.
	 *
	 * @param syndesisRealmName
	 * @param syndesisClientName
	 */
	public static void updateSyndesisUiClientConf(String syndesisRealmName, String syndesisClientName) {

		final ClientsResource clientsResource = kc.realm(syndesisRealmName).clients();
		ClientRepresentation syndesisUiClient = null;

		final List<ClientRepresentation> clients = clientsResource.findAll();

		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getClientId().equals(syndesisClientName)) {

				syndesisUiClient = clients.get(i);
				syndesisUiClient.setDirectAccessGrantsEnabled(Boolean.TRUE);
			}
		}
		final String id = syndesisUiClient.getId();

		final String url = keycloakUrl + "/admin/realms/" + syndesisRealmName + "/clients/" + id;
		final Invocation.Builder invocation = client
				.target(url)
				.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + getAdminToken());

		final Response response = invocation.put(Entity.entity(syndesisUiClient, javax.ws.rs.core.MediaType.APPLICATION_JSON));
	}

	/**
	 * Deletes kc user.
	 *
	 * @param realmName
	 * @param userName
	 */
	public static void deleteKcUser(String realmName, String userName) {

		if (checkIfUserExists(realmName, userName)) {
			final String userId = getUserId(realmName, userName);
			final String url = keycloakUrl + "/admin/realms/" + realmName + "/users/" + userId;

			final Invocation.Builder invocation = client
					.target(url)
					.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + getAdminToken());

			invocation.delete();
		} else {
			log.debug("User didn't exist in first place. Nothing to do...");
		}
	}

	/**
	 * Creates kc user. It assignes also the federated IDP's needed for testing.
	 *
	 * @param syndesisRealmName
	 * @param syndesisClientName
	 * @param userName
	 * @param userPassword
	 * @param userEmail
	 * @param firstName
	 * @param lastName
	 * @param openShiftUid
	 * @param openShiftUname
	 * @param githubUid
	 * @param githubUname
	 */
	public static void createUser(String syndesisRealmName, String syndesisClientName, String userName, String userPassword, String userEmail, String firstName,
			String lastName, String openShiftUid, String openShiftUname, int githubUid, String githubUname) {
		final UserRepresentation user = new UserRepresentation();

		user.setUsername(userName);
		user.setEmail(userEmail);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setRequiredActions(Collections.<String>emptyList());
		user.setEnabled(true);
		user.setRealmRoles(Arrays.asList("offline_access"));

		final CredentialRepresentation rawPassword = new CredentialRepresentation();
		rawPassword.setValue(userPassword);
		rawPassword.setType(CredentialRepresentation.PASSWORD);
		user.setCredentials(Arrays.asList(rawPassword));

		final ClientsResource clientsResource = kc.realm(syndesisRealmName).clients();
		ClientRepresentation syndesisUiClient = null;
		final List<ClientRepresentation> clients = clientsResource.findAll();

		for (int i = 0; i < clients.size(); i++) {
			log.debug(clients.get(i).getClientId());
			if (clients.get(i).getClientId().equals(syndesisClientName)) {

				syndesisUiClient = clients.get(i);
				syndesisUiClient.setDirectAccessGrantsEnabled(Boolean.TRUE);
			}
		}

		final String url = keycloakUrl + "/admin/realms/" + syndesisRealmName + "/users?realm=" + syndesisRealmName;

		final Invocation.Builder invocation = client
				.target(url)
				.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + getAdminToken());

		final Response response = invocation.post(Entity.entity(user, javax.ws.rs.core.MediaType.APPLICATION_JSON));
		final String userId = getUserId(syndesisRealmName, userName);

		updateUserPassword(syndesisRealmName, syndesisClientName, userName, userPassword, userId);
		addFederatedIdentity(syndesisRealmName, "openshift", userId, openShiftUid, openShiftUname);
		addFederatedIdentity(syndesisRealmName, "github", userId, Integer.toString(githubUid), githubUname);
		final String brokerClientId = getBrokerClientId(syndesisRealmName);
		assignBrokerClientRoles(syndesisRealmName, brokerClientId, userId);
	}

	/**
	 * Will increase the access token and access token for implicit flow lifespan.
	 *
	 * @param realmName
	 * @param ttl - time in seconds the ttl will be increased to
	 * @throws JsonProcessingException
	 */
	private static void setSyndesisAccessTokenLifespan(String realmName, int ttl) {

		final RealmResource realmResource = kc.realm(realmName);
		final RealmRepresentation realmRepresentation = realmResource.toRepresentation();

		realmRepresentation.setAccessTokenLifespan(ttl);
		realmRepresentation.setAccessTokenLifespanForImplicitFlow(ttl);
		realmResource.update(realmRepresentation);
	}

	/**
	 * Finds the keycloak user ID using his user name.
	 *
	 * @param syndesisRealmName
	 * @param userName
	 * @return
	 */
	private static String getUserId(String syndesisRealmName, String userName) {

		UserRepresentation user = null;
		user = kc.realm(syndesisRealmName).users().search(userName, 0, 1).get(0);

		return user.getId();
	}

	/**
	 * Finds the keycloak client ID using it's name.
	 *
	 * @param syndesisRealmName
	 * @param userName
	 * @return
	 */
	private static String getSyndesisUiClientId(String syndesisRealmName, String syndesisClientName) {
		String syndesisUiClientId = null;
		final List<ClientRepresentation> clients = kc.realm(syndesisRealmName).clients().findAll();

		for (ClientRepresentation c : clients) {
			if (c.getClientId().equals(syndesisClientName)) {
				syndesisUiClientId = c.getClientId();
			}
		}
		return syndesisUiClientId;
	}

	/**
	 * As the user can't be for some reason created with default password already, we must use second call to give
	 * existing user his default pass.
	 *
	 * @param syndesisRealmName
	 * @param syndesisClientName
	 * @param userName
	 * @param userPassword
	 * @param userId
	 */
	private static void updateUserPassword(String syndesisRealmName, String syndesisClientName, String userName, String userPassword, String userId) {

		final String url = keycloakUrl + "/admin/realms/" + syndesisRealmName + "/users/" + userId + "/reset-password";

		final CredentialRepresentation credentials = new CredentialRepresentation();
		credentials.setType("password");
		credentials.setValue(userPassword);
		final List<CredentialRepresentation> list = new ArrayList();
		list.add(credentials);

		final Invocation.Builder invocation = client
				.target(url)
				.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + getAdminToken());

		final Response response = invocation.put(Entity.entity(credentials, javax.ws.rs.core.MediaType.APPLICATION_JSON));
	}

	/**
	 * Used for adding IDP links to existing keycloak user.
	 *
	 * @param syndesisRealmName
	 * @param syndesisClientName
	 * @param idpName
	 * @param userId
	 * @param federatedIdpId
	 * @param federatedIdentityUname
	 */
	private static void addFederatedIdentity(String syndesisRealmName, String idpName, String userId, String federatedIdpId, String federatedIdentityUname) {

		final FederatedIdentityRepresentation idm = new FederatedIdentityRepresentation();
		idm.setIdentityProvider(idpName);
		idm.setUserId(federatedIdpId);
		idm.setUserName(federatedIdentityUname);

		final String url = keycloakUrl + "/admin/realms/" + syndesisRealmName + "/users/" + userId + "/federated-identity/" + idpName;
		final Invocation.Builder invocation = client
				.target(url)
				.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + getAdminToken());

		final Response response = invocation.post(Entity.entity(idm, javax.ws.rs.core.MediaType.APPLICATION_JSON));
	}

	private static String getBrokerClientId(String syndesisRealmName) {

		ClientRepresentation brokerClient = null;

		brokerClient = kc.realm(syndesisRealmName).clients().findByClientId("broker").get(0);

		log.debug(brokerClient.getId());

		return brokerClient.getId();
	}

	private static List<RoleRepresentation> getBrokerAvailableRoleMappings(String syndesisRealmName, String brokerClientId, String userId) {

		final List<RoleRepresentation> roles = kc.realm(syndesisRealmName).users().get(userId).roles().clientLevel(brokerClientId).listAvailable();

		return roles;
	}

	private static void assignBrokerClientRoles(String syndesisRealmName, String brokerClientId, String userId) {

		final List<RoleRepresentation> brokerRoleRepresentation = getBrokerAvailableRoleMappings(syndesisRealmName, brokerClientId, userId);
		final String url = keycloakUrl + "/admin/realms/" + syndesisRealmName + "/users/" + userId + "/role-mappings/clients/" + brokerClientId;

		final Invocation.Builder invocation = client
				.target(url)
				.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + getAdminToken());

		final Response response = invocation.post(Entity.entity(brokerRoleRepresentation, javax.ws.rs.core.MediaType.APPLICATION_JSON));
	}
}
