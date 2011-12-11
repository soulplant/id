package com.id.editor;

import com.id.editor.Visual.Mode;
import com.id.file.FileView;

public class Register {
  private final Mode mode;
  private final String[] lines;
  private final boolean lineBreakOnLast;

  public Register(Visual.Mode mode, boolean lineBreakOnLast, String... lines) {
    this.mode = mode;
    this.lineBreakOnLast = lineBreakOnLast;
    this.lines = lines;
  }

  public void put(int y, int x, FileView file) {
    switch (mode) {
    case CHAR:
      file.insertText(y, x, lineBreakOnLast, lines);
      break;
    case LINE:
      file.insertLines(y, lines);
      break;
    }
  }
}
