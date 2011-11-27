package com.id.file;

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
    FileView file = new FileView(new File("a"));
    file.splitLine(0, 1);
    assertEquals("a", file.getLine(0));
    assertEquals("", file.getLine(1));
  }

  @Test
  public void splitInMiddle() {
    FileView file = new FileView(new File("abc"));
    file.splitLine(0, 1);
    assertEquals("a", file.getLine(0));
    assertEquals("bc", file.getLine(1));
  }

  @Test
  public void removeText() {
    FileView file = new FileView(new File("abc"));
    String removedText = file.removeText(0, 1, 1);
    assertEquals("b", removedText);
    assertEquals("ac", file.getLine(0));
  }

  @Test
  public void removeLineRange() {
    FileView file = new FileView(new File("abc", "def"));
    file.removeLineRange(0, 1);
  }

  @Test
  public void highlights() {
    FileView file = new FileView(new File("abc", "def"));
    file.setHighlight("abc");
    assertTrue(file.isHighlighted(0, 0));
    assertFalse(file.isHighlighted(1, 0));
    file.changeLine(0, "babc");
    assertFalse(file.isHighlighted(0, 0));
    assertTrue(file.isHighlighted(0, 1));
    file.clearHighlight();
    assertFalse(file.isHighlighted(0, 0));
    assertFalse(file.isHighlighted(0, 1));
  }

  @Test
  public void getWordUnderCursor() {
    FileView file = new FileView(new File("abc asdf", "def"));
    assertEquals("abc", file.getWordUnder(0, 1));
  }

  @Test
  public void getNextHighlightPoint() {
    FileView file = new FileView(new File("abc asdf", "def", "abc"));
    file.setHighlight("abc");
    Point point = file.getNextHighlightPoint(0, 0);
    assertEquals(2, point.getY());
    assertEquals(0, point.getX());
  }
}
