package com.atipera.demo.controller;

import com.atipera.demo.dto.ErrorResponse;
import com.atipera.demo.dto.RepositoryDetails;
import com.atipera.demo.dto.UserNotFoundException;
import com.atipera.demo.service.GitHubService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/github")
public class GitHubController {

  private final GitHubService gitHubService;

  public GitHubController(GitHubService gitHubService) {
    this.gitHubService = gitHubService;
  }

  @GetMapping("/repos/{username}")
  public List<RepositoryDetails> getRepositories(@PathVariable String username) {
    return gitHubService.getRepositories(username);
  }

  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
    return new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
  }
}