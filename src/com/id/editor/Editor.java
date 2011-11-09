package com.id.editor;

import com.id.file.FileView;

public class Editor {
  public interface Context {
    void moveScreenToIncludePoint(Point point);
    void recenterScreenOnPoint(Point point);
  }

  private final FileView file;
  private final Cursor cursor = new Cursor();
  private final Context context;
  private boolean inInsertMode = false;

  public Editor(FileView fileView, Context context) {
    this.file = fileView;
    this.context = context;
  }

  public String getLine(int y) {
    return file.getLine(y);
  }

  public int getLineCount() {
    return file.getLineCount();
  }

  public Point getCursorPosition() {
    return cursor.getPoint();
  }

  // Keyboard commands.
  public void down() {
    cursor.moveBy(1, 0);
    applyCursorConstraints();
  }


  public void up() {
    cursor.moveBy(-1, 0);
    applyCursorConstraints();
  }

  public void left() {
    cursor.moveBy(0, -1);
    applyCursorConstraints();
  }

  public void right() {
    cursor.moveBy(0, 1);
    applyCursorConstraints();
  }

  public void insert() {
    inInsertMode = true;
    cursor.setDefaultX(cursor.getX());
  }

  private void applyCursorConstraints() {
    int isInNormalMode = !isInInsertMode() ? 1 : 0;
    cursor.constrainY(0, file.getLineCount() - 1);
    cursor.constrainX(0, getCurrentLine().length() - isInNormalMode);
  }

  private String getCurrentLine() {
    return file.getLine(cursor.getY());
  }

  public boolean isInInsertMode() {
    return inInsertMode;
  }

  public void escape() {
    inInsertMode = false;
  }

  public void onLetterTyped(char keyChar) {
    if (!file.isInPatch()) {
      startPatch();
    }

    String text = "" + keyChar;
    file.insertText(cursor.getY(), cursor.getX(), text);
    cursor.moveBy(0, text.length());
  }

  private void startPatch() {
    file.startPatchAt(cursor.getY(), cursor.getX());
  }
}
