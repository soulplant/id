package com.id.file;

import com.id.editor.Point;

public interface Highlight extends File.Listener {
  boolean isHighlighted(int y, int x);
  Point getNextMatch(int y, int x);
  Point getPreviousMatch(int y, int x);
  int getMatchCount();
  void setHighlightPattern(String pattern);
}
