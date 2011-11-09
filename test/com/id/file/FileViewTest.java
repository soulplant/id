package com.id.file;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FileViewTest {
  @Test
  public void viewShrinksWhenLinesGetRemoved() {
    FileView fileView = new FileView(makeFileWithLines("a", "b", "c"), 0, 1);

    fileView.removeLine(1);
    assertEquals(1, fileView.getLineCount());

    fileView.removeLine(1);
    assertEquals(1, fileView.getLineCount());
  }

  @Test
  public void viewGrowsWhenLinesGetInserted() {
    FileView fileView = new FileView(makeFileWithLines("a", "b", "c"));
    fileView.insertLine(0, "hi");
    assertEquals(4, fileView.getLineCount());
    fileView.insertLine(4, "there");
    assertEquals(5, fileView.getLineCount());
  }

  private File makeFileWithLines(String... lines) {
    int i = 0;
    File file = new File();
    for (String line : lines) {
      file.insertLine(i, line);
      i++;
    }
    return file;
  }
}
