package com.id.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.id.editor.Editor;
import com.id.events.EditorKeyHandler;
import com.id.events.KeyStroke;
import com.id.file.File;
import com.id.file.FileView;

public class EditorTestBase {
  protected EditorKeyHandler handler;
  protected Editor editor;
  protected FileView fileView;
  protected File file;
  protected String[] lastFileContents;

  protected void ensureUndoGoesToLastFileContents() {
    type(KeyStroke.escape());
    while (editor.hasUndo()) {
      typeString("u");
    }
    assertFileContents(lastFileContents);
  }

  protected void setFileContents(String... lines) {
    lastFileContents = lines;
    file = new File(lines);
    fileView = new FileView(file);
    editor = new Editor(fileView);
    handler = new EditorKeyHandler();
  }

  protected void assertFileContents(String... lines) {
    assertArrayEquals(lines, file.getLines());
  }

  protected void assertCursorPosition(int y, int x) {
    assertEquals(y, editor.getCursorPosition().getY());
    assertEquals(x, editor.getCursorPosition().getX());
  }

  protected void type(KeyStroke event) {
    handler.handleKeyPress(event, editor);
  }

  protected void typeString(String letters) {
    for (int i = 0; i < letters.length(); i++) {
      typeChar(letters.charAt(i));
    }
  }

  protected void typeChar(char c) {
    handler.handleKeyPress(KeyStroke.fromChar(c), editor);
  }
}
