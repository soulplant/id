package com.id.events;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.id.editor.Editor;
import com.id.file.File;
import com.id.file.FileView;

public class EditorKeyHandlerTest {
  private File file;
  private FileView fileView;
  private Editor editor;
  private EditorKeyHandler handler;

  @Before
  public void setup() {
    file = new File();
    fileView = new FileView(file);
    editor = new Editor(fileView);
    handler = new EditorKeyHandler();
  }

  @Test
  public void testHandling() {
    // NOTE This will break if we ever add a key binding for q in the editor.
    assertFalse(handler.handleKeyPress(KeyStroke.fromChar('q'), editor));
  }
}
