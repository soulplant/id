package com.id.app;

import com.id.editor.Editor;


public class ViewportTracker {

  private String filename = null;
  private int linesFromTop = -1;

  private boolean isTracking = true;
  private final FocusManager focusManager;
  private int y = -1;
  private int x = -1;

  public ViewportTracker(FocusManager focusManager) {
    this.focusManager = focusManager;
  }

  public void saveViewport(String filename, int linesFromTop, int y, int x) {
    this.filename = filename;
    this.linesFromTop = linesFromTop;
    this.y = y;
    this.x = x;
    isTracking = false;
  }

  public void restoreViewport() {
    Editor editor = focusManager.focusEditor(filename, linesFromTop);
    editor.moveCursorTo(y, x);
    isTracking = true;
  }

  public boolean isTracking() {
    return isTracking;
  }
}
