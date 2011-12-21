package com.id.git;

import com.id.app.Shell;

public class GitRepository implements Repository {
  private final Shell shell;

  public GitRepository(Shell shell) {
    this.shell = shell;
  }

  @Override
  public String getHead() {
    return "HEAD";
  }

  @Override
  public Diff getDiffTo(String rev) {
    Diff lines = Diff.fromLines(shell.exec("git diff " + getHead()));
    System.out.println(lines);
    return lines;
  }

  @Override
  public void commitAll(String message) {
    shell.exec("git add .");
    // TODO(koz): Make work for multi-word commit messages.
    shell.exec("git commit -am " + message);
  }

  @Override
  public void init() {
    shell.exec("git init");
  }
}
