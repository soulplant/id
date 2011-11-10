package com.id.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.id.editor.Point;
import com.id.file.File;
import com.id.file.Tombstone;

public class FileTest {
  @Test
  public void insertLine() {
    File file = new File();
    file.insertLine(0, "HI");
    assertEquals("HI", file.getLine(0));
    file.insertLine(0, "there");
    assertEquals("there", file.getLine(0));
    file.changeLine(0, "you");
    assertEquals("you", file.getLine(0));
    assertEquals(2, file.getLineCount());
    file.removeLine(0);
    assertEquals(1, file.getLineCount());
    assertEquals("HI", file.getLine(0));
  }

  @Test
  public void undo() {
    File file = new File();
    file.startPatchAt(new Point(0, 0));
    file.insertLine(0, "HI");
    file.insertLine(0, "there");
    file.breakPatch();
    file.undo();
    assertEquals(0, file.getLineCount());
    assertTrue(isAllStatus(Tombstone.Status.NORMAL, file));
    file.redo();
    assertEquals(2, file.getLineCount());
    assertEquals("there", file.getLine(0));
    assertEquals("HI", file.getLine(1));
  }

  @Test
  public void splitAtEnd() {
    File file = new File("a");
    file.splitLine(0, 1);
    assertEquals("a", file.getLine(0));
    assertEquals("", file.getLine(1));
  }

  @Test
  public void splitInMiddle() {
    File file = new File("abc");
    file.splitLine(0, 1);
    assertEquals("a", file.getLine(0));
    assertEquals("bc", file.getLine(1));
  }

  @Test
  public void removeText() {
    File file = new File("abc");
    String removedText = file.removeText(0, 1, 1);
    assertEquals("b", removedText);
    assertEquals("ac", file.getLine(0));
  }

  private static boolean isAllStatus(Tombstone.Status status, File file) {
    for (int i = 0; i < file.getLineCount(); i++) {
      if (file.getStatus(i) != status) {
        return false;
      }
    }
    return true;
  }
}
