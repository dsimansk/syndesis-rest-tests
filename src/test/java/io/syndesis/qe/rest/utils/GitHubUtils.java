package io.syndesis.qe.rest.utils;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import io.syndesis.qe.rest.exceptions.GitHubException;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * Utility methods for GitHub.
 *
 * @author jknetl
 */
@Slf4j
public class GitHubUtils {

	/**
	 * Deletes a github repository with given name
	 *
	 * @param gitHub connected GitHub object
	 * @param GhRepoFullName full name of a GitHub repository (e.g. john/my-super-repository)
	 * @throws GitHubException If repository is found but cannot be deleted.
	 */
	public static void deleteRepositories(GitHub gitHub, String GhRepoFullName) throws GitHubException {
		GHRepository repository = null;
		try {
			repository = gitHub.getRepository(GhRepoFullName);
			try {
				repository.delete();
				log.info("Deleting GitHub repository {}", repository.getFullName());
			} catch (IOException e) {
				throw new GitHubException("Cannot delete repository " + repository.getFullName(), e);
			}
		} catch (IOException e) {
			log.error("Repository '{}' cannot be found. No repository will be deleted.", GhRepoFullName);
		}
	}

	/**
	 * Github user will be registered with the github oauth app.
	 *
	 * @param gitHub - we need the github client created using uname/password login, otherwise we would get this
	 * message: {"message":"This API can only be accessed with username and password Basic
	 * Auth","documentation_url":"https://developer.github.com/v3/oauth_authorizations/#oauth-authorizations-api"}
	 * @param githubClientId - github oauth app client ID, we can get it here: https://github.com/settings/developers
	 * @param githubClientSecret - github oauth app client secret, we can get here:
	 * https://github.com/settings/developers
	 */
	public static void registerAppWithGithubUser(GitHub gitHub, String githubClientId, String githubClientSecret) {

		final List<String> scopes = Arrays.asList("public_repo", "user:email");
		final String note = "script";

		try {
			gitHub.createOrGetAuth(githubClientId, githubClientSecret, scopes, note, "");
		} catch (IOException ex) {
			log.error("There was a problem invoking the github api" + ex);
			throw new GitHubException("Issue invoking the github API: ", ex);
		}
	}

	/**
	 * Get github user ID.
	 *
	 * @param gitHub
	 * @param githubUserName
	 * @return
	 */
	public static int getGithubUserId(GitHub gitHub, String githubUserName) {
		int id = 0;

		try {
			id = gitHub.getUser(githubUserName).getId();
		} catch (IOException ex) {
			log.error("There was a problem invoking the github api " + ex);
			throw new GitHubException("Issue invoking the github API: ", ex);
		}

		return id;
	}
}
