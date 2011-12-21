package com.id.git;

public class InMemoryRepository implements Repository {
  private Diff nextDiffResult;

  @Override
  public String getHead() {
    return "HEAD";
  }

  @Override
  public Diff getDiffTo(String rev) {
    return nextDiffResult;
  }

  public void setDiffResult(Diff diff) {
    this.nextDiffResult = diff;
  }

  @Override
  public void commitAll(String message) {
    // Do nothing.
  }

  @Override
  public void init() {
    // Do nothing.
  }
}
