package com.id.file;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.id.editor.Editor;

public class EditorTest {
  // TODO Add a test about empty file behaviour.
  @Test
  public void backspaceAtStartOfFile() {
    File file = new File("a");
    FileView fileView = new FileView(file);
    Editor editor = new Editor(fileView);

    editor.insert();
    editor.backspace();
    assertEquals("a", file.getLine(0));
  }

  @Test
  public void backspaceOnlyLetter() {
    File file = new File("a");
    FileView fileView = new FileView(file);
    Editor editor = new Editor(fileView);

    editor.append();
    editor.backspace();
    assertEquals("", file.getLine(0));
  }

  @Test
  public void undoDoesntPutCursorOutsideCorrectRange() {
    File file = new File("abcdefg");
    FileView fileView = new FileView(file);
    Editor editor = new Editor(fileView);

    editor.appendEnd();
    editor.onLetterTyped('a');
    editor.onLetterTyped('b');
    editor.escape();
    editor.undo();
    assertEquals(6, editor.getCursorPosition().getX());
  }
}
