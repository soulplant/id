package com.id.editor;

import com.id.file.FileView;

public class Editor {
  public interface Context {
    void moveScreenToIncludePoint(Point point);
    void recenterScreenOnPoint(Point point);
  }

  private final FileView file;
  private final Cursor cursor = new Cursor();
  private boolean inInsertMode = false;

  public Editor(FileView fileView) {
    this.file = fileView;
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
    cursor.constrainX(0, getCurrentLineLength() - isInNormalMode);
  }

  private String getCurrentLine() {
    return file.getLine(cursor.getY());
  }

  public boolean isInInsertMode() {
    return inInsertMode;
  }

  public void escape() {
    if (isInInsertMode()) {
      inInsertMode = false;
      file.breakPatch();
      cursor.moveBy(0, -1);
      applyCursorConstraints();
    }
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

  public void undo() {
    Point position = file.undo();
    if (position == null) {
      return;
    }
    cursor.moveTo(position.getY(), position.getX());
    applyCursorConstraints();
  }

  public void redo() {
    Point position = file.redo();
    if (position == null) {
      return;
    }
    cursor.moveTo(position.getY(), position.getX());
    applyCursorConstraints();
  }

  public void addEmptyLine() {
    if (!file.isInPatch()) {
      startPatch();
    }
    file.insertLine(Math.min(file.getLineCount(), cursor.getY() + 1), "");
    cursor.moveBy(1, 0);
    applyCursorConstraints();
    insert();
  }

  public void addEmptyLinePrevious() {
    startPatch();
    file.insertLine(cursor.getY(), "");
    applyCursorConstraints();
    insert();
  }

  public void backspace() {
    if (!file.isInPatch()) {
      startPatch();
    }
    if (cursor.getX() == 0) {
      if (cursor.getY() == 0) {
        return;
      }
      String line = getCurrentLine();
      file.removeLine(cursor.getY());
      cursor.moveBy(-1, 0);
      int targetX = getCurrentLine().length();
      file.insertText(cursor.getY(), targetX, line);
      cursor.moveTo(cursor.getY(), targetX);
    } else {
      file.removeText(cursor.getY(), cursor.getX() - 1, 1);
      cursor.moveBy(0, -1);
    }
  }

  public void append() {
    insert();
    cursor.moveBy(0, 1);
    applyCursorConstraints();
  }

  public void appendEnd() {
    cursor.moveTo(cursor.getY(), getCurrentLineLength());
    insert();
  }

  private int getCurrentLineLength() {
    if (file.isEmpty()) {
      return 0;
    }
    return getCurrentLine().length();
  }

  public void enter() {
    assert isInInsertMode();
    if (!file.isInPatch()) {
      startPatch();
    }
    if (file.isEmpty()) {
      addEmptyLine();
      return;
    }
    file.splitLine(cursor.getY(), cursor.getX());
    cursor.moveBy(1, 0);
    cursor.moveTo(cursor.getY(), 0);
  }
}
