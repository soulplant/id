package com.id.editor;

import static org.junit.Assert.*;

import java.awt.event.KeyEvent;

import org.junit.Before;
import org.junit.Test;

import com.id.events.EditorKeyHandler;
import com.id.file.File;
import com.id.file.FileView;

public class EditorTypingTest {
  private EditorKeyHandler handler;
  private Editor editor;
  private FileView fileView;
  private File file;

  @Before
  public void init() {
    setFileContents();
  }

  private void setFileContents(String... lines) {
    file = new File(lines);
    fileView = new FileView(file);
    editor = new Editor(fileView);
    handler = new EditorKeyHandler();
  }

  @Test
  public void changeLine() {
    setFileContents("abc");
    typeString("lCabc");
    type(handler.escape());
    assertEquals("aabc", file.getLine(0));
    assertFalse(editor.isInInsertMode());
  }

  @Test
  public void deleteLine() {
    setFileContents("abc");
    typeString("D");
    assertEquals("", file.getLine(0));
    assertFalse(editor.isInInsertMode());
    typeString("u");
    assertEquals("abc", file.getLine(0));
  }

  private void type(KeyEvent event) {
    handler.handleKeyPress(event, editor);
  }

  private void typeString(String letters) {
    for (int i = 0; i < letters.length(); i++) {
      typeChar(letters.charAt(i));
    }
  }

  private void typeChar(char c) {
    handler.handleChar(c, editor);
  }
}
