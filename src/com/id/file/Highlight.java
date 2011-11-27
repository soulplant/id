package com.id.file;

import com.id.editor.Point;

public interface Highlight extends File.Listener {
  boolean isHighlighted(int y, int x);
  Point getNextMatch(int y, int x);
}
