package com.id.editor;


public class EmptyHighlight implements Highlight {
  @Override
  public boolean isHighlighted(int y, int x) {
    return false;
  }

  @Override
  public void onLineInserted(int y, String line) {
    // Do nothing.
  }

  @Override
  public void onLineRemoved(int y, String line) {
    // Do nothing.
  }

  @Override
  public void onLineChanged(int y, String oldLine, String newLine) {
    // Do nothing.
  }

  @Override
  public Point getNextMatch(int y, int x) {
    return null;
  }

  @Override
  public Point getPreviousMatch(int y, int x) {
    return null;
  }

  @Override
  public int getMatchCount() {
    return 0;
  }
}
