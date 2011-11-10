package com.id.editor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.id.file.File;
import com.id.file.FileView;

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

  @Test
  public void emptyFileInsert() {
    File file = new File();
    FileView fileView = new FileView(file);
    Editor editor = new Editor(fileView);

    editor.insert();
    editor.onLetterTyped('a');
    assertEquals("a", file.getLine(0));
  }

  @Test
  public void emptyFileAppend() {
    File file = new File();
    FileView fileView = new FileView(file);
    Editor editor = new Editor(fileView);

    editor.append();
    editor.onLetterTyped('a');
    assertEquals("a", file.getLine(0));
  }

  @Test
  public void emptyFileAddEmptyLine() {
    File file = new File();
    FileView fileView = new FileView(file);
    Editor editor = new Editor(fileView);

    editor.addEmptyLine();
    editor.onLetterTyped('a');
    assertEquals("a", file.getLine(0));
  }

  @Test
  public void emptyFileAddEmptyLinePrevious() {
    File file = new File();
    FileView fileView = new FileView(file);
    Editor editor = new Editor(fileView);

    editor.addEmptyLinePrevious();
    editor.onLetterTyped('a');
    assertEquals("a", file.getLine(0));
  }
}
