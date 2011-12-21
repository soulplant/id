package com.id.editor;

import com.id.file.File;

public interface Highlight extends File.Listener {
  boolean isHighlighted(int y, int x);
  Point getNextMatch(int y, int x);
  Point getPreviousMatch(int y, int x);
  int getMatchCount();
}
