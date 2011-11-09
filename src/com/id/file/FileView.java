package com.id.file;

import com.id.editor.Point;

public class FileView implements File.Listener {
  private final File file;
  private final int start;
  private final int end;

  public FileView(File file, int start, int end) {
    this.file = file;
    this.start = start;
    this.end = end;
  }

  public String getLine(int y) {
    return file.getLine(start + y);
  }

  public void insertLine(int y, String line) {
    file.insertLine(start + y, line);
  }

  @Override
  public void onLineInserted(int y, String line) {

  }

  @Override
  public void onLineRemoved(int y, String line) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onLineChanged(int y, String oldLine, String newLine) {
    // TODO Auto-generated methodj stub

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
}
