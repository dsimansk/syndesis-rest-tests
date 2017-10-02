package io.syndesis.qe.rest.utils;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.nio.file.Paths;

import io.syndesis.qe.rest.accounts.Account;
import io.syndesis.qe.rest.accounts.AccountsDirectory;
import io.syndesis.qe.rest.utils.selenide.FirstTimeLoginFlow;
import lombok.extern.slf4j.Slf4j;

/**
 * Default user utils.
 *
 * Sep 8, 2017 Red Hat
 *
 * @author tplevko@redhat.com
 */
@Slf4j
public final class DefaultUserUtil {

	private static AccountsDirectory accountsDirectory;
	private static Account githubAccount;
	private static Account openshiftAccount;
	private static Account keycloakAccount;
	private static Account syndesisAccount;
	private static GitHub gitHub;
	private static DefaultUserUtil defaultUserUtil = null;

	public static DefaultUserUtil getInstance() {
		if (defaultUserUtil == null) {
			defaultUserUtil = new DefaultUserUtil();
		}
		return defaultUserUtil;
	}

	private DefaultUserUtil() {
		accountsDirectory = new AccountsDirectory(Paths.get(SyndesisRestConstants.ACCOUNT_CONFIG_PATH));
		openshiftAccount = accountsDirectory.getAccount("openshift").get();
		keycloakAccount = accountsDirectory.getAccount("keycloak").get();
		githubAccount = accountsDirectory.getAccount("github").get();
		syndesisAccount = accountsDirectory.getAccount("syndesis").get();

		final GitHubBuilder builder = new GitHubBuilder();
		try {
			gitHub = builder.withPassword(githubAccount.getProperty("login"), githubAccount.getProperty("password")).build();
		} catch (IOException ex) {
			log.error(ex.toString());
		}
	}

	/**
	 * Used to create user and delete user if already exists.
	 *
	 * @throws IOException
	 */
	public static void forcePrepareDefaultUser() throws IOException {
		prepareDefaultUser(true);
	}

	/**
	 * if "force" is used - delete existing "default user" and create a new one with the same credentials.
	 *
	 * @param forcePrepare
	 */
	public static void prepareDefaultUser(boolean forcePrepare) {

		if (forcePrepare) {
			deleteKcUser(keycloakAccount.getProperty("syndesisRealmName"), syndesisAccount.getProperty("login"));
		}
		//TODO(tplevko): use pepa's tool he provided for registering github oauth apps - you will be able to do this
		// without properties in conf file.
		//first - register the user with the Github OAUTH app registered for sindesis
		final String openShiftUserId = getOpenShiftUserId();
		final int githubUserId = GitHubUtils.getGithubUserId(gitHub, githubAccount.getProperty("login"));
		registerUserWithKeycloakOauthApp(openShiftUserId, syndesisAccount.getProperty("login"), githubUserId, githubAccount.getProperty("login"));
		executeFirstTimeLoginFlow();
	}

	/**
	 * Check if the OS UID property is set. If yes, don't try to create the OS user. If not, the user is not yet created
	 * and we use probably the minishift syndesis deployment.
	 *
	 * @return
	 */
	public static boolean openShiftUserExists() {

		return false;
	}

	//TODO(tplevko): if there is no "githubOauthAppId" property set in the properties file, we will create the github oauth app using "pepa's tool"
	private static void prepareGithubOauthApp() {
	}

	public static void deleteKcUser(String syndesisRealmName, String keycloakUserName) {
		KeycloakUtils.deleteKcUser(syndesisRealmName, keycloakUserName);
	}

	/**
	 * Try using keycloak client to verify, that the user is already registered.
	 */
	public boolean defaultUserExists(String syndesisRealmName, String keycloakUserName) {
		return KeycloakUtils.checkIfUserExists(syndesisRealmName, keycloakUserName);
	}

	public static void executeFirstTimeLoginFlow() {

		final String githubUname = githubAccount.getProperty("login");
		final String githubPassword = githubAccount.getProperty("password");
		final String osName = syndesisAccount.getProperty("login");
		final String osPass = syndesisAccount.getProperty("password");
		final String openShiftUrl = openshiftAccount.getProperty("instanceUrl");
		final String syndesisUrl = syndesisAccount.getProperty("instanceUrl");

		FirstTimeLoginFlow.githubLogin(githubUname, githubPassword);
		FirstTimeLoginFlow.openShiftLogin(osName, osPass, openShiftUrl);
		FirstTimeLoginFlow.syndesisLogin(osName, osPass, syndesisUrl);
	}

	public static void registerUserWithGithubOauthApp() {

		//TODO(tplevko): cases to secure - user is already registered? App doesn't exist?
		final String githubOauthAppId = githubAccount.getProperty("githubOauthAppId");
		final String githubOauthAppSecret = githubAccount.getProperty("githubOauthAppSecret");

		GitHubUtils.registerAppWithGithubUser(gitHub, githubOauthAppId, githubOauthAppSecret);
	}

	/**
	 * There are two options: 1. we already have the OS user created, and we know his UID - we won't create him new -
	 * this is especially the case for syndesis test env. 2. The user UID is not specified, and the user doesn't exist,
	 * so we will need a user with cluster-admin role and we will create a new user -- this is the case for minishift.
	 *
	 * @return new openShift UID
	 */
	public static String getOpenShiftUserId() {

		String openshiftUid = openshiftAccount.getProperty("userUID");

		if (openshiftUid.isEmpty()) {
			final String openShiftUserName = syndesisAccount.getProperty("login");
			openshiftUid = OpenShiftUtils.getInstance().getOpenShiftUserId(openShiftUserName);
		}
		return openshiftUid;
	}

	public static void registerUserWithKeycloakOauthApp(String openshiftUid, String openshiftUname, int githubUid, String githubUname) {

		//TODO(tplevko): cases to secure - user is already registered? App doesn't exist?
		final String kcSyndesisRealm = keycloakAccount.getProperty("syndesisRealmName");
		final String kcSyndesisClient = keycloakAccount.getProperty("syndesisClientName");
		final String kcTestUname = syndesisAccount.getProperty("login");
		final String kcTestPass = syndesisAccount.getProperty("password");
		final String kcTestFirstName = keycloakAccount.getProperty("kcFirstName");
		final String kcTestLastName = keycloakAccount.getProperty("kcLastName");
		final String kcTestEmail = keycloakAccount.getProperty("kcEmail");

		KeycloakUtils.getInstance().getAdminToken();
		KeycloakUtils.getInstance().updateSyndesisUiClientConf(kcSyndesisRealm, kcSyndesisClient);
		KeycloakUtils.getInstance().createUser(kcSyndesisRealm, kcSyndesisClient, kcTestUname, kcTestPass, kcTestEmail, kcTestFirstName, kcTestLastName, openshiftUid,
				openshiftUname, githubUid, githubUname);
	}

	public static String getDefaultUserToken() {

		final String kcSyndesisRealm = keycloakAccount.getProperty("syndesisRealmName");
		final String kcSyndesisClient = keycloakAccount.getProperty("syndesisClientName");
		final String kcTestUname = syndesisAccount.getProperty("login");
		final String kcTestPass = syndesisAccount.getProperty("password");
		final String userTokenUrl = keycloakAccount.getProperty("userTokenUrl");
		String token;

		if (!KeycloakUtils.getInstance().checkIfUserExists(kcSyndesisRealm, kcTestUname)) {

			prepareDefaultUser(false);
		}
		token = KeycloakUtils.getInstance().getUserToken(userTokenUrl, kcTestUname, kcTestPass, kcSyndesisClient, kcSyndesisRealm);
		return token;
	}
}
