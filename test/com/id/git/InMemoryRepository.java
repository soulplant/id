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
}
