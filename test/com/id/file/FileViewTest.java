package com.id.file;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.id.test.EditorTestBase;

public class FileViewTest extends EditorTestBase {
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
    setFileContents("a", "b", "c");
    fileView.insertLine(0, "hi");
    assertEquals(4, fileView.getLineCount());
    fileView.insertLine(4, "there");
    assertEquals(5, fileView.getLineCount());
  }

  @Test
  public void splitAtEnd() {
    setFileContents("a");
    fileView.splitLine(0, 1, "xxx");
    assertEquals("a", fileView.getLine(0));
    assertEquals("xxx", fileView.getLine(1));
  }

  @Test
  public void splitInMiddle() {
    setFileContents("abc");
    fileView.splitLine(0, 1, "xxx");
    assertEquals("a", fileView.getLine(0));
    assertEquals("xxxbc", fileView.getLine(1));
  }

  @Test
  public void removeText() {
    setFileContents("abc");
    String removedText = fileView.removeText(0, 1, 1);
    assertEquals("b", removedText);
    assertEquals("ac", fileView.getLine(0));
  }

  @Test
  public void removeLineRange() {
    setFileContents("abc", "def");
    fileView.removeLineRange(0, 1);
  }

  @Test
  public void getWordUnderCursor() {
    setFileContents("abc asdf", "def");
    assertEquals("abc", fileView.getWordUnder(0, 1));
  }

  @Test
  public void insertText() {
    setFileContents("ab");
    fileView.insertText(0, 1, "xxx");
    assertFileContents("axxxb");
  }

  @Test
  public void insertMultilineText() {
    setFileContents("ab");
    fileView.insertText(0, 1, "xxx", "yyy");
    assertFileContents("axxx", "yyyb");
  }

  @Test
  public void insertMultilineTextWithTheLastLineIncludingALineBreak() {
    setFileContents("ab");
    fileView.insertTextWithLineBreak(0, 1, "xxx", "yyy");
    assertFileContents("axxx", "yyy", "b");
  }

  @Test
  public void insertLines() {
    setFileContents("abc");
    fileView.insertLines(1, "def", "ghi");
    assertFileContents("abc", "def", "ghi");
  }

  @Test
  public void insertMultilineTextOnEmpty() {
    setFileContents();
    fileView.insertText(0, 0, "abc", "def");
    assertFileContents("abc", "def");
  }

  @Test
  public void insertMultilineTextWithTrailingLine() {
    setFileContents();
    fileView.insertTextWithLineBreak(0, 0, "abc", "def");
    assertFileContents("abc", "def");
  }

  @Test
  public void findNextChar() {
    setFileContents("this is a test");
    assertEquals(4, fileView.findNextLetter(0, 0, ' '));
    assertEquals(7, fileView.findNextLetter(0, 4, ' '));
  }

  @Test
  public void findPreviousChar() {
    setFileContents("abcded");
    assertEquals(3, fileView.findPreviousLetter(0, 5, 'd'));
  }
}
