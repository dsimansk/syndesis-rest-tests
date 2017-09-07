package io.syndesis.qe.rest.utils;

import io.syndesis.qe.rest.exceptions.GitHubException;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;

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
			log.info("Repository '{}' cannot be found. No repository will be deleted.", GhRepoFullName);
		}
	}
}
