package com.id.editor;

import com.id.editor.Visual.Mode;
import com.id.file.FileView;

public class TextFragment {
  private final Mode mode;
  private final String[] lines;
  private final boolean lineBreakOnLast;

  public TextFragment(Visual.Mode mode, boolean lineBreakOnLast, String... lines) {
    this.mode = mode;
    this.lineBreakOnLast = lineBreakOnLast;
    this.lines = lines;
  }

  public void put(int y, int x, FileView file) {
    switch (mode) {
    case CHAR:
      file.insertText(y, x + 1, lineBreakOnLast, lines);
      break;
    case LINE:
      file.insertLines(y + 1, lines);
      break;
    }
  }

  public void putBefore(int y, int x, FileView file) {
    switch (mode) {
    case CHAR:
      file.insertText(y, x, lines);
      break;
    case LINE:
      file.insertLines(y, lines);
      break;
    }
  }

  public int getLineCount() {
    return lines.length;
  }

  public String getLine(int y) {
    return lines[y];
  }

  public Mode getMode() {
    return mode;
  }
}
