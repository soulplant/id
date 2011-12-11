package com.id.editor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.id.file.File;
import com.id.file.FileView;


public class RegisterTest {
  @Test
  public void insertsCharwiseContentsIntoALine() {
    Register register = new Register(Visual.Mode.CHAR, false, "woo");
    File file = new File("abc", "def");
    FileView fileView = new FileView(file);
    register.put(0, 1, fileView);
    assertEquals("awoobc", fileView.getLine(0));
  }

  @Test
  public void insertsLinewiseContentsIntoLines() {
    Register register = new Register(Visual.Mode.LINE, false, "woo", "a", "test");
    File file = new File("abc", "def");
    FileView fileView = new FileView(file);
    register.put(1, 0, fileView);
    assertFileContents(fileView, "abc", "woo", "a", "test", "def");
    assertEquals("woo", fileView.getLine(1));
    assertEquals("a", fileView.getLine(2));
    assertEquals("test", fileView.getLine(3));
  }

  private void assertFileContents(FileView fileView, String... contents) {
    assertEquals(contents.length, fileView.getLineCount());
    for (int i = 0; i < contents.length; i++) {
      assertEquals(contents[i], fileView.getLine(i));
    }
  }

  @Test
  public void insertMultilineCharwiseContentsInsertsLinebreaks() {
    Register register = new Register(Visual.Mode.CHAR, false, "woo", "a", "test");
    File file = new File("abc", "def");
    FileView fileView = new FileView(file);
    register.put(0, 2, fileView);
    assertFileContents(fileView, "abwoo", "a", "testc", "def");
  }

  @Test
  public void emptyRegistersDoNothing() {
    Register register = new Register(Visual.Mode.CHAR, false);
    File file = new File("abc", "def");
    FileView fileView = new FileView(file);
    register.put(0, 2, fileView);
    assertFileContents(fileView, "abc", "def");
  }
}
