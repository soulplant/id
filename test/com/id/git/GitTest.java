package com.id.git;

import org.junit.Test;

public class GitTest {
  @Test
  public void stuff() {
    GitRepo repo = new GitRepo();
    List<Diff> patches = repo.getLog(2);

  }
}
