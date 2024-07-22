package com.atipera.demo.service;

import com.atipera.demo.dto.BranchDetails;
import com.atipera.demo.dto.GitHubBranch;
import com.atipera.demo.dto.GitHubRepo;
import com.atipera.demo.dto.RepositoryDetails;
import com.atipera.demo.dto.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubService {

  private final WebClient webClient;

  public GitHubService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl("https://api.github.com").build();
  }

  public List<RepositoryDetails> getRepositories(String username) {
    try {
      Mono<GitHubRepo[]> response = webClient.get()
          .uri("/users/" + username + "/repos")
          .retrieve()
          .bodyToMono(GitHubRepo[].class);

      GitHubRepo[] repos = response.block();
      if (repos != null) {
        return Arrays.stream(repos)
            .filter(repo -> !repo.isFork())
            .map(repo -> {
              List<BranchDetails> branches = getBranches(username, repo.getName());
              return new RepositoryDetails(repo.getName(), repo.getOwner().getLogin(), branches);
            })
            .collect(Collectors.toList());
      } else {
        throw new RuntimeException("Failed to fetch repositories");
      }
    } catch (WebClientResponseException.NotFound e) {
      throw new UserNotFoundException("User not found", e);
    }
  }

  private List<BranchDetails> getBranches(String username, String repoName) {
    Mono<GitHubBranch[]> response = webClient.get()
        .uri("/repos/" + username + "/" + repoName + "/branches")
        .retrieve()
        .bodyToMono(GitHubBranch[].class);

    GitHubBranch[] branches = response.block();
    if (branches != null) {
      return Arrays.stream(branches)
          .map(branch -> new BranchDetails(branch.getName(), branch.getCommit().getSha()))
          .collect(Collectors.toList());
    } else {
      throw new RuntimeException("Failed to fetch branches");
    }
  }
}
