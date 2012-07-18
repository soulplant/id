package com.id.git;

import java.util.ArrayList;
import java.util.List;

public class InMemoryRepository implements Repository {
  private Diff nextDiffResult;

  @Override
  public String getHead() {
    return "HEAD";
  }

  @Override
  public Diff getDiffRelativeTo(String rev) {
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

  @Override
  public List<String> getRevisionList() {
    return new ArrayList<String>();
  }
}
