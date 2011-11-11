package com.id.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.id.file.File;
import com.id.file.FileView;

public class EditorTest {
  private File file;
  private FileView fileView;
  private Editor editor;

  @Before
  public void init() {
    setFileContents();
  }

  private void setFileContents(String... lines) {
    file = new File(lines);
    fileView = new FileView(file);
    editor = new Editor(fileView);
  }

  @Test
  public void backspaceAtStartOfFile() {
    setFileContents("a");

    editor.insert();
    editor.backspace();
    assertEquals("a", file.getLine(0));
  }


  @Test
  public void backspaceOnlyLetter() {
    setFileContents("a");

    editor.append();
    editor.backspace();
    assertEquals("", file.getLine(0));
  }

  @Test
  public void undoDoesntPutCursorOutsideCorrectRange() {
    setFileContents("abcdefg");

    editor.appendEnd();
    editor.onLetterTyped('a');
    editor.onLetterTyped('b');
    editor.escape();
    editor.undo();
    assertEquals(6, editor.getCursorPosition().getX());
  }

  @Test
  public void emptyFileInsert() {
    editor.insert();
    editor.onLetterTyped('a');
    assertEquals("a", file.getLine(0));
  }

  @Test
  public void emptyFileAppend() {
    editor.append();
    editor.onLetterTyped('a');
    assertEquals("a", file.getLine(0));
  }

  @Test
  public void emptyFileAddEmptyLine() {
    editor.addEmptyLine();
    editor.onLetterTyped('a');
    assertEquals("a", file.getLine(1));
  }

  @Test
  public void emptyFileAddEmptyLinePrevious() {
    editor.addEmptyLinePrevious();
    editor.onLetterTyped('a');
    assertEquals("a", file.getLine(0));
  }

  @Test
  public void enter() {
    editor.insert();
    editor.onLetterTyped('a');
    editor.enter();
    assertEquals("a", file.getLine(0));
    assertEquals("", file.getLine(1));
  }

  @Test
  public void undoEnter() {
    setFileContents("a", "b");
    editor.append();
    editor.enter();
    assertEquals("a", file.getLine(0));
    assertEquals("", file.getLine(1));
    assertEquals("b", file.getLine(2));
    editor.escape();
    editor.undo();
    assertEquals("a", file.getLine(0));
    assertEquals("b", file.getLine(1));
  }

  @Test
  public void enterPutsCursorOnRightLine() {
    setFileContents("abc");
    editor.append();
    editor.enter();
    assertEquals(0, editor.getCursorPosition().getX());
  }

  @Test
  public void addEmptyLineOnEmptyFile() {
    editor.addEmptyLine();
    assertEquals("", file.getLine(0));
    assertEquals("", file.getLine(1));
    assertEquals(1, editor.getCursorPosition().getY());
    assertEquals(0, editor.getCursorPosition().getX());
  }

  @Test
  public void addEmptyLinePreviousOnEmptyFile() {
    editor.addEmptyLinePrevious();
    editor.onLetterTyped('a');
    assertEquals("a", file.getLine(0));
    assertEquals("", file.getLine(1));
    assertEquals(0, editor.getCursorPosition().getY());
    assertEquals(1, editor.getCursorPosition().getX());
  }

  @Test
  public void range() {
    setFileContents("abc", "d", "efg");
    editor.toggleVisual(Visual.Mode.LINE);
    assertTrue(editor.isInVisual(0, 0));
    editor.toggleVisual(Visual.Mode.LINE);
    assertFalse(editor.isInVisual(0, 0));
    editor.toggleVisual(Visual.Mode.LINE);
    assertTrue(editor.isInVisual(0, 0));
    editor.escape();
    assertFalse(editor.isInVisual(0, 0));
  }

  @Test
  public void toggleBetweenLineAndChar() {
    setFileContents("abc");
    editor.right();
    editor.toggleVisual(Visual.Mode.LINE);
    editor.right();
    editor.toggleVisual(Visual.Mode.CHAR);
    assertTrue(editor.isInVisual(0, 1));
  }
}
