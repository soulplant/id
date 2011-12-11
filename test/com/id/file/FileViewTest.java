package com.id.file;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.id.editor.Point;

public class FileViewTest {
  @Test
  public void viewShrinksWhenLinesGetRemoved() {
    FileView fileView = new FileView(new File("a", "b", "c"), 0, 1);

    fileView.removeLine(1);
    assertEquals(1, fileView.getLineCount());

    fileView.removeLine(1);
    assertEquals(1, fileView.getLineCount());
  }

  @Test
  public void viewGrowsWhenLinesGetInserted() {
    FileView fileView = new FileView(new File("a", "b", "c"));
    fileView.insertLine(0, "hi");
    assertEquals(4, fileView.getLineCount());
    fileView.insertLine(4, "there");
    assertEquals(5, fileView.getLineCount());
  }

  @Test
  public void splitAtEnd() {
    FileView fileView = new FileView(new File("a"));
    fileView.splitLine(0, 1);
    assertEquals("a", fileView.getLine(0));
    assertEquals("", fileView.getLine(1));
  }

  @Test
  public void splitInMiddle() {
    FileView fileView = new FileView(new File("abc"));
    fileView.splitLine(0, 1);
    assertEquals("a", fileView.getLine(0));
    assertEquals("bc", fileView.getLine(1));
  }

  @Test
  public void removeText() {
    FileView fileView = new FileView(new File("abc"));
    String removedText = fileView.removeText(0, 1, 1);
    assertEquals("b", removedText);
    assertEquals("ac", fileView.getLine(0));
  }

  @Test
  public void removeLineRange() {
    FileView fileView = new FileView(new File("abc", "def"));
    fileView.removeLineRange(0, 1);
  }

  @Test
  public void highlights() {
    FileView fileView = new FileView(new File("abc", "def"));
    fileView.setHighlight("abc");
    assertTrue(fileView.isHighlighted(0, 0));
    assertFalse(fileView.isHighlighted(1, 0));
    fileView.changeLine(0, "babc");
    assertFalse(fileView.isHighlighted(0, 0));
    assertTrue(fileView.isHighlighted(0, 1));
    fileView.clearHighlight();
    assertFalse(fileView.isHighlighted(0, 0));
    assertFalse(fileView.isHighlighted(0, 1));
  }

  @Test
  public void getWordUnderCursor() {
    FileView fileView = new FileView(new File("abc asdf", "def"));
    assertEquals("abc", fileView.getWordUnder(0, 1));
  }

  @Test
  public void getNextHighlightPoint() {
    FileView fileView = new FileView(new File("abc asdf", "def", "abc"));
    fileView.setHighlight("abc");
    Point point = fileView.getNextHighlightPoint(0, 0);
    assertEquals(2, point.getY());
    assertEquals(0, point.getX());
  }

  @Test
  public void insertText() {
    File file = new File("ab");
    FileView fileView = new FileView(file);
    fileView.insertText(0, 1, "xxx");
    assertFileContents(file, "axxxb");
  }

  @Test
  public void insertMultilineText() {
    File file = new File("ab");
    FileView fileView = new FileView(file);
    System.out.println(file.getLineList());
    fileView.insertText(0, 1, "xxx", "yyy");
    System.out.println(file.getLineList());
    assertFileContents(file, "axxx", "yyyb");
  }

  @Test
  public void insertMultilineTextWithTheLastLineIncludingALineBreak() {
    File file = new File("ab");
    FileView fileView = new FileView(file);
    fileView.insertTextWithLineBreak(0, 1, "xxx", "yyy");
    assertFileContents(file, "axxx", "yyy", "b");
  }

  @Test
  public void insertLines() {
    File file = new File("abc");
    FileView fileView = new FileView(file);
    fileView.insertLines(1, "def", "ghi");
    assertFileContents(file, "abc", "def", "ghi");
  }

  private void assertFileContents(File file, String... contents) {
    assertArrayEquals(contents, file.getLines());
  }
}
