package com.id.file;

import com.id.editor.Point;
import com.id.file.Tombstone.Status;

public class FileView implements File.Listener, ModifiedListener {
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

  @Override
  public void onModifiedStateChanged() {
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

  public String removeText(int y, int x, int length) {
    return file.removeText(start + y, x, length);
  }

  public String removeText(int y, int x) {
    return file.removeText(start + y, x);
  }

  public void removeLine(int y) {
    file.removeLine(start + y);
  }

  public boolean isEmpty() {
    return getLineCount() == 0;
  }

  public void splitLine(int y, int x) {
    file.splitLine(start + y, x);
  }

  public String getFilename() {
    return file.getFilename();
  }

  public Status getStatus(int y) {
    return file.getStatus(start + y);
  }

  public Grave getGrave(int y) {
    return file.getGrave(start + y);
  }

  public void removeLineRange(int from, int to) {
    file.removeLineRange(start + from, start + to);
  }

  public void appendToLine(int y, String tail) {
    file.appendToLine(start + y, tail);
  }
}
