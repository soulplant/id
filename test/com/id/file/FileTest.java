package com.id.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import com.id.editor.Point;
import com.id.platform.InMemoryFileSystem;

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
  public void otherTest() {
    File file = new File("abc");
    assertFalse(file.isModified());
    ModifiedListener listener = mock(ModifiedListener.class);
    file.addModifiedListener(listener);
    file.startPatchAt(0, 0);
    file.insertLine(0, "hi");
    verify(listener, atLeastOnce()).onModifiedStateChanged();
    assertTrue(file.isModified());
  }

  @Test
  public void loadFileTest() throws IOException {
    StringReader stringReader = new StringReader("this\nis");
    File file = File.loadFrom(new BufferedReader(stringReader));
    assertEquals("this", file.getLine(0));
    assertEquals("is", file.getLine(1));
    assertEquals(2, file.getLineCount());
    assertTrue(isAllStatus(Tombstone.Status.NORMAL, file));
  }

  @Test
  public void isModified() {
    File file = new File("a");
    assertFalse(file.isModified());
    file.startPatchAt(0, 0);
    file.insertLine(0, "b");
    assertTrue(file.isModified());
    file.breakPatch();
    assertTrue(file.isModified());
  }

  @Test
  public void saveFiresModifiedChangedEvent() {
    File file = new File("a");
    file.setFilename("<temp>");

    file.startPatchAt(0, 0);
    file.insertLine(0, "hi");
    file.breakPatch();
    ModifiedListener listener = mock(ModifiedListener.class);
    file.addModifiedListener(listener);
    file.save(new InMemoryFileSystem());
    verify(listener).onModifiedStateChanged();
    assertFalse(file.isModified());
  }

  @Test
  public void undoLineInsertLine() {
    File file = new File();
    file.insertLine(0, "hi");
    file.undoLine(0);
    assertEquals(0, file.getLineCount());
  }

  @Test
  public void makeView() {
    File file = new File();
    file.insertLine(0, "HI");
    file.insertLine(1, "there");
    file.insertLine(2, "yo");
    FileView view = file.makeView(1, 2);
    view.getLineList();
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
