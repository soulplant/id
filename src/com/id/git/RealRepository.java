package com.id.git;

import com.id.app.Shell;

public class RealRepository implements Repository {
  private final Shell shell;

  public RealRepository(Shell shell) {
    this.shell = shell;
  }

  @Override
  public String getHead() {
    return "HEAD";
  }

  @Override
  public Diff getDiffTo(String rev) {
    return Diff.fromLines(shell.exec("git diff " + getHead()));
  }
}
