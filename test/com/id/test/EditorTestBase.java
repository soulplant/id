package com.id.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.id.app.HighlightState;
import com.id.editor.Editor;
import com.id.editor.Register;
import com.id.events.EditorKeyHandler;
import com.id.events.KeyStroke;
import com.id.file.File;
import com.id.file.FileView;
import com.id.file.Tombstone;

public class EditorTestBase {
  protected EditorKeyHandler handler;
  protected Editor editor;
  protected FileView fileView;
  protected File file;
  protected String[] lastFileContents;
  private boolean okForChangeMarkersToBeInconsistentAfterUndo = false;

  protected void ensureUndoGoesToLastFileContents() {
    type(KeyStroke.escape());
    while (editor.hasUndo()) {
      typeString("u");
    }
    assertFileContents(lastFileContents);
    if (!okForChangeMarkersToBeInconsistentAfterUndo) {
      assertAllStatus(Tombstone.Status.NORMAL);
    }
  }

  protected void setOkForChangeMarkersToBeInconsistentAfterUndo() {
    okForChangeMarkersToBeInconsistentAfterUndo = true;
  }

  protected void setFileContents(String... lines) {
    lastFileContents = lines;
    file = new File(lines);
    fileView = new FileView(file);
    editor = new Editor(fileView, new HighlightState(), new Register());
    handler = new EditorKeyHandler();
  }

  protected void assertFileContents(String... lines) {
    assertArrayEquals(lines, file.getLines());
  }

  protected void assertLineContents(int y, String line) {
    assertEquals(line, editor.getLine(y));
  }

  protected void assertCursorPosition(int y, int x) {
    assertEquals("cursor.y", y, editor.getCursorPosition().getY());
    assertEquals("cursor.x", x, editor.getCursorPosition().getX());
  }

  protected void type(KeyStroke keyStroke) {
    handler.handleKeyPress(keyStroke, editor);
  }

  protected void typeString(String letters) {
    for (KeyStroke stroke : KeyStroke.fromString(letters)) {
      type(stroke);
    }
  }

  protected void typeChar(char c) {
    type(KeyStroke.fromChar(c));
  }

  protected void assertAllStatus(Tombstone.Status status) {
    for (int y = 0; y < editor.getLineCount(); y++) {
      assertEquals(status, editor.getStatus(y));
    }
  }
}
