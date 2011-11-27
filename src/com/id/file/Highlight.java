package com.id.file;

public interface Highlight extends File.Listener {
  boolean isHighlighted(int y, int x);
}
