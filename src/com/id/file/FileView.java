package com.id.file;

import com.id.editor.Point;

public class FileView implements File.Listener {
  private final File file;
  private int start;
  private int end;

  public FileView(File file) {
    this(file, 0, file.getLineCount() - 1);
  }
  public FileView(File file, int start, int end) {
    this.file = file;
    this.start = start;
    this.end = end;
    // TODO Make this not leak.
    file.addListener(this);
  }

  public String getLine(int y) {
    return file.getLine(start + y);
  }

  public void insertLine(int y, String line) {
    file.insertLine(start + y, line);
  }

  @Override
  public void onLineInserted(int y, String line) {
    if (y < start) {
      start++;
      end++;
    } else if (start <= y && y <= end + 1) {
      end++;
    }
  }

  @Override
  public void onLineRemoved(int y, String line) {
    if (y < start) {
      start--;
      end--;
    } else if (start <= y && y <= end) {
      end--;
    }
  }

  @Override
  public void onLineChanged(int y, String oldLine, String newLine) {
    // Do nothing.
  }

  public int getLineCount() {
    return end - start + 1;
  }

  public boolean isInPatch() {
    return file.isInPatch();
  }

  public void startPatchAt(int y, int x) {
    file.startPatchAt(start + y, x);
  }

  public void insertText(int y, int x, String text) {
    file.insertText(start + y, x, text);
  }

  public void breakPatch() {
    file.breakPatch();
  }

  public Point undo() {
    return file.undo();
  }

  public Point redo() {
    return file.redo();
  }

  public void removeText(int y, int x, int length) {
    file.removeText(start + y, x, length);
  }

  public void removeLine(int y) {
    file.removeLine(start + y);
  }

  public boolean isEmpty() {
    return getLineCount() == 0;
  }
}
